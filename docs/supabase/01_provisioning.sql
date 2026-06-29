-- ============================================================
-- Semtex · Provisión de usuarios (paso #3)
-- ============================================================
-- Qué hace: cuando alguien se registra en Supabase Auth, crea
-- automáticamente su fila en public.users (con el MISMO id que
-- auth.users, imprescindible para la integridad de los FK) y, en
-- self-signup, una organización nueva de la que es ADMIN.
--
-- Forward-compatible con invitaciones: si el signup trae en el
-- metadata 'organization_id' (+ 'app_role'), el usuario se une a esa
-- organización existente en vez de crear una nueva.
--
-- CÓMO EJECUTAR: pega TODO este archivo en el SQL Editor de Supabase
-- (Dashboard → SQL Editor → New query → Run). Se ejecuta como rol
-- 'postgres', que tiene permisos para crear el trigger sobre auth.users.
-- Es idempotente: puedes re-ejecutarlo sin romper nada.
-- ============================================================

create or replace function public.handle_new_user()
returns trigger
language plpgsql
security definer
set search_path = public
as $$
declare
  v_company text;
  v_slug    text;
  v_org_id  uuid;
  v_role    user_role;
begin
  -- ¿Invitación? Si el metadata trae una organización, se une a ella.
  v_org_id := nullif(new.raw_user_meta_data->>'organization_id', '')::uuid;

  if v_org_id is null then
    -- ---- Self-signup: empresa nueva, el usuario es ADMIN ----
    v_role := 'ADMIN';

    -- Nombre de empresa: del metadata 'company_name', o derivado del email.
    v_company := coalesce(
      nullif(trim(new.raw_user_meta_data->>'company_name'), ''),
      split_part(new.email, '@', 1)
    );

    -- Slug URL-friendly y único (cumple ^[a-z0-9-]{2,100}$).
    v_slug := regexp_replace(lower(v_company), '[^a-z0-9]+', '-', 'g');
    v_slug := trim(both '-' from v_slug);
    if length(v_slug) < 2 then
      v_slug := 'org';
    end if;
    -- Sufijo corto del id para evitar colisiones de slug.
    v_slug := left(v_slug, 90) || '-' || substr(replace(new.id::text, '-', ''), 1, 8);

    insert into public.organizations (name, slug)
    values (v_company, v_slug)
    returning id into v_org_id;
  else
    -- ---- Invitación: rol del metadata (default OPERATOR) ----
    v_role := coalesce(nullif(new.raw_user_meta_data->>'app_role', ''), 'OPERATOR')::user_role;
  end if;

  -- Fila en public.users con el MISMO id que auth.users (clave para los FK).
  insert into public.users (id, email, role, organization_id)
  values (new.id, new.email, v_role, v_org_id)
  on conflict (id) do nothing;

  return new;
end;
$$;

-- Dispara en cada alta de Supabase Auth.
drop trigger if exists on_auth_user_created on auth.users;
create trigger on_auth_user_created
  after insert on auth.users
  for each row execute function public.handle_new_user();
