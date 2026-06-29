-- ============================================================
-- Semtex · Custom Access Token Hook (paso #2)
-- ============================================================
-- Qué hace: al emitir el JWT (en cada login/refresh), busca en
-- public.users la fila del usuario e inyecta en el token los claims
-- 'org_id' y 'app_role' que el backend necesita para el tenant y los
-- permisos. Usamos 'app_role' (NO 'role') porque Supabase ya pone
-- role='authenticated'.
--
-- CÓMO EJECUTAR:
--   1) Pega TODO este archivo en el SQL Editor de Supabase y Run.
--   2) Luego ACTÍVALO en el dashboard:
--      Authentication → Hooks → "Customize Access Token (JWT) Claims"
--      → elige la función public.custom_access_token_hook → Enable.
--   3) Los tokens viejos NO cambian: hay que volver a iniciar sesión
--      para obtener uno con los nuevos claims.
-- ============================================================

create or replace function public.custom_access_token_hook(event jsonb)
returns jsonb
language plpgsql
stable
as $$
declare
  claims   jsonb;
  v_org_id uuid;
  v_role   text;
begin
  -- El id del usuario viene en event->>'user_id'.
  select organization_id, role::text
    into v_org_id, v_role
    from public.users
   where id = (event->>'user_id')::uuid;

  claims := event->'claims';

  if v_org_id is not null then
    claims := jsonb_set(claims, '{org_id}',   to_jsonb(v_org_id::text));
    claims := jsonb_set(claims, '{app_role}', to_jsonb(v_role));
  end if;

  event := jsonb_set(event, '{claims}', claims);
  return event;
end;
$$;

-- ---- Permisos: el hook corre como rol 'supabase_auth_admin' ----
grant usage on schema public to supabase_auth_admin;
grant execute on function public.custom_access_token_hook(jsonb) to supabase_auth_admin;
grant select on public.users to supabase_auth_admin;

-- Que NADIE más pueda ejecutar el hook directamente.
revoke execute on function public.custom_access_token_hook(jsonb) from authenticated, anon, public;
