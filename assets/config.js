const URL_DESARROLLO = "http://127.0.0.1:8080";
const URL_PRODUCCION = "https://bodeguita-backend.onrender.com";

function esEntornoDesarrollo() {
  const host = window.location.hostname;

  return (
    host === "localhost" ||
    host === "127.0.0.1" ||
    host.startsWith("192.168.") ||
    host.startsWith("10.")
  );
}

export const API_BASE = esEntornoDesarrollo() ? URL_DESARROLLO : URL_PRODUCCION;

export const FACTILIZA_API_BASE = "https://api.factiliza.com/v1";
export const FACTILIZA_API_TOKEN =
  "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI0MjU5MSJ9.ezjFpoGRlvNp4lgJ6UuC5-K7jfdmm1Tt5h961VLeRrg";
