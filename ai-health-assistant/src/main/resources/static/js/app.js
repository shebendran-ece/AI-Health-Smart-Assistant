(function () {
  const API = ""; // same-origin; the backend serves this file, so relative paths work

  // ---------------- Pulse dividers between sections ----------------
  function pulseDividerSVG(seed) {
    const p = `M0,17 L${60 + seed},17 L${75 + seed},4 L${90 + seed},30 L${105 + seed},17 L800,17`;
    return `<svg viewBox="0 0 800 34" preserveAspectRatio="none"><path d="${p}"/></svg>`;
  }
  document.querySelectorAll("section.module").forEach((sec, i) => {
    if (i === 0) return;
    const div = document.createElement("div");
    div.className = "divider";
    div.setAttribute("aria-hidden", "true");
    div.innerHTML = pulseDividerSVG(i * 40);
    sec.parentNode.insertBefore(div, sec);
  });

  let state = { bmi: null };

  async function api(path, options) {
    const res = await fetch(API + path, {
      headers: { "Content-Type": "application/json" },
      ...options,
    });
    if (!res.ok) {
      const body = await res.json().catch(() => ({}));
      throw new Error(body.error || `Request failed: ${res.status}`);
    }
    return res.status === 204 ? null : res.json();
  }

  // ---------------- Snapshot ----------------
  function updateSnapshotFromWater(cups) {
    document.getElementById("snapWater").innerHTML = `${cups}<span>/8 cups</span>`;
  }
  function updateSnapshotFromMeds(meds) {
    const pending = meds.filter((m) => !m.taken).length;
    document.getElementById("snapMeds").textContent = pending;
  }
  function updateSnapshotFromVitals(vitals) {
    if (vitals.length) {
      const last = vitals[0]; // list comes back newest-first
      document.getElementById("snapVital").textContent = `${last.value} ${last.unit}`;
    }
  }
  function updateSnapshotFromBmi() {
    document.getElementById("snapBmi").textContent = state.bmi ? state.bmi.toFixed(1) : "—";
  }

  // ---------------- Symptom Assistant ----------------
  const chatLog = document.getElementById("chatLog");
  const chatInput = document.getElementById("chatInput");
  const chatSend = document.getElementById("chatSend");
  const chipRow = document.getElementById("chipRow");

  function addMsg(text, cls, tag) {
    const div = document.createElement("div");
    div.className = "msg " + cls;
    if (tag) {
      const t = document.createElement("span");
      t.className = "tag";
      t.textContent = tag;
      div.appendChild(t);
    }
    const body = document.createElement("div");
    body.textContent = text;
    div.appendChild(body);
    chatLog.appendChild(div);
    chatLog.scrollTop = chatLog.scrollHeight;
  }

  async function handleUserText(text) {
    if (!text.trim()) return;
    addMsg(text, "user");
    chatSend.disabled = true;
    try {
      const result = await api("/api/symptom-check", {
        method: "POST",
        body: JSON.stringify({ message: text }),
      });
      const cls = result.urgent ? "bot urgent" : "bot";
      const tag = result.urgent ? "Urgent" : result.source === "gemini" ? "Gemini" : "General guidance";
      addMsg(result.reply, cls, tag);
    } catch (err) {
      addMsg("Something went wrong reaching the assistant: " + err.message, "bot urgent", "Error");
    } finally {
      chatSend.disabled = false;
    }
  }

  chatSend.addEventListener("click", () => {
    const text = chatInput.value;
    chatInput.value = "";
    handleUserText(text);
  });
  chatInput.addEventListener("keydown", (e) => {
    if (e.key === "Enter") {
      const text = chatInput.value;
      chatInput.value = "";
      handleUserText(text);
    }
  });

  ["Headache since morning", "Mild fever", "Trouble sleeping", "Feeling anxious"].forEach((c) => {
    const b = document.createElement("button");
    b.className = "chip";
    b.textContent = c;
    b.addEventListener("click", () => handleUserText(c));
    chipRow.appendChild(b);
  });

  addMsg(
    "Hi, I'm the Pulseline assistant. Tell me what's going on and I'll share general guidance — I'm not a substitute for a clinician, especially for anything urgent.",
    "bot",
    "Assistant"
  );

  // ---------------- Vitals ----------------
  const vitalLog = document.getElementById("vitalLog");

  function renderVitals(vitals) {
    vitalLog.innerHTML = "";
    if (!vitals.length) {
      const li = document.createElement("li");
      li.className = "empty";
      li.textContent = "No readings logged yet.";
      vitalLog.appendChild(li);
      return;
    }
    vitals.forEach((v) => {
      const li = document.createElement("li");
      li.innerHTML = `<span>${labelFor(v.type)}</span><span class="${v.flagged ? "flag" : "ok"}">${v.value} ${v.unit} ${
        v.flagged ? "· outside typical range" : "· typical range"
      }</span>`;
      vitalLog.appendChild(li);
    });
  }
  function labelFor(type) {
    return { bp: "Systolic BP", hr: "Resting HR", temp: "Temperature", spo2: "SpO₂" }[type] || type;
  }

  async function loadVitals() {
    const vitals = await api("/api/vitals");
    renderVitals(vitals);
    updateSnapshotFromVitals(vitals);
  }

  document.getElementById("vAdd").addEventListener("click", async () => {
    const type = document.getElementById("vType").value;
    const value = parseFloat(document.getElementById("vVal").value);
    if (isNaN(value)) return;
    await api("/api/vitals", { method: "POST", body: JSON.stringify({ type, value }) });
    document.getElementById("vVal").value = "";
    loadVitals();
  });

  // ---------------- BMI (client-side only) ----------------
  document.getElementById("bCalc").addEventListener("click", () => {
    const h = parseFloat(document.getElementById("bHeight").value) / 100;
    const w = parseFloat(document.getElementById("bWeight").value);
    if (!h || !w) return;
    const bmi = w / (h * h);
    state.bmi = bmi;
    let band, pct;
    if (bmi < 18.5) { band = "Underweight"; pct = (bmi / 18.5) * 20; }
    else if (bmi < 25) { band = "Typical range"; pct = 20 + ((bmi - 18.5) / 6.5) * 35; }
    else if (bmi < 30) { band = "Above typical range"; pct = 55 + ((bmi - 25) / 5) * 25; }
    else { band = "Well above typical range"; pct = 80 + Math.min((bmi - 30) / 10, 1) * 20; }
    pct = Math.max(2, Math.min(98, pct));
    document.getElementById("bmiResult").style.display = "block";
    document.getElementById("bmiNum").textContent = bmi.toFixed(1);
    document.getElementById("bmiBand").textContent = band;
    document.getElementById("bmiMarker").style.left = pct + "%";
    updateSnapshotFromBmi();
  });

  // ---------------- Water ----------------
  const dropTrack = document.getElementById("dropTrack");
  const waterLabel = document.getElementById("waterLabel");
  for (let i = 0; i < 8; i++) {
    const d = document.createElement("div");
    d.className = "drop";
    d.dataset.i = i;
    d.title = "Cup " + (i + 1);
    dropTrack.appendChild(d);
  }

  function renderWater(cups) {
    dropTrack.querySelectorAll(".drop").forEach((d, idx) => d.classList.toggle("filled", idx < cups));
    waterLabel.textContent = `${cups} of 8 cups`;
    updateSnapshotFromWater(cups);
  }

  async function loadWater() {
    const log = await api("/api/water/today");
    renderWater(log.cups);
  }

  dropTrack.addEventListener("click", async (e) => {
    if (!e.target.classList.contains("drop")) return;
    // simple model: clicking always adds one cup (server caps at 8); refresh to sync
    await api("/api/water/increment", { method: "POST" });
    loadWater();
  });

  // ---------------- Medications ----------------
  const medList = document.getElementById("medList");

  function renderMeds(meds) {
    medList.innerHTML = "";
    meds.forEach((m) => {
      const li = document.createElement("li");
      li.className = "med-item" + (m.taken ? " done" : "");
      li.innerHTML = `<input type="checkbox" ${m.taken ? "checked" : ""} aria-label="Mark ${m.name} as taken">
        <span class="med-name">${m.name}</span><span class="med-time mono">${m.time}</span>`;
      const cb = li.querySelector("input");
      cb.addEventListener("change", async () => {
        await api(`/api/medications/${m.id}/toggle`, { method: "PUT" });
        loadMeds();
      });
      medList.appendChild(li);
    });
  }

  async function loadMeds() {
    const meds = await api("/api/medications");
    renderMeds(meds);
    updateSnapshotFromMeds(meds);
  }

  document.getElementById("medAdd").addEventListener("click", async () => {
    const name = document.getElementById("medName").value.trim();
    const time = document.getElementById("medTime").value.trim();
    if (!name) return;
    await api("/api/medications", { method: "POST", body: JSON.stringify({ name, time }) });
    document.getElementById("medName").value = "";
    document.getElementById("medTime").value = "";
    loadMeds();
  });

  // ---------------- Init ----------------
  Promise.all([loadVitals(), loadWater(), loadMeds()]).catch((err) => {
    console.error("Failed to load initial data:", err);
  });
})();
