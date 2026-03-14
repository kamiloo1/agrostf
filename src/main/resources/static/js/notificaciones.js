/**
 * Notificaciones - campana del header.
 * Carga notificaciones vía API /api/notificaciones y muestra dropdown.
 */
(function() {
  'use strict';

  const API_BASE = '/api/notificaciones';

  function loadNotifications() {
    const list = document.getElementById('notif-list');
    const loading = document.getElementById('notif-loading');
    const empty = document.getElementById('notif-empty');
    const badge = document.getElementById('notif-badge');

    if (!list) return;

    fetch(API_BASE, { credentials: 'same-origin' })
      .then(r => {
        if (!r.ok) throw new Error('HTTP ' + r.status);
        return r.json();
      })
      .then(data => {
        loading.style.display = 'none';
        const items = data.notificaciones || [];
        const totalNoLeidas = data.totalNoLeidas || 0;

        if (badge) {
          badge.textContent = totalNoLeidas > 99 ? '99+' : totalNoLeidas;
          badge.style.display = totalNoLeidas > 0 ? 'flex' : 'none';
        }

        if (items.length === 0) {
          empty.style.display = 'block';
          list.innerHTML = '';
          list.appendChild(empty);
          return;
        }

        empty.style.display = 'none';
        list.innerHTML = '';
        items.forEach(n => {
          const div = document.createElement('div');
          div.className = 'notif-item' + (n.leida ? '' : ' notif-no-leida');
          div.dataset.id = n.id;
          div.innerHTML = `
            <div class="notif-item-mensaje">${escapeHtml(n.mensaje)}</div>
            <div class="notif-item-meta">${formatFecha(n.fechaCreacion)}</div>
          `;
          if (n.enlace) {
            div.style.cursor = 'pointer';
            div.addEventListener('click', () => {
              if (!n.leida) marcarLeida(n.id);
              window.location.href = n.enlace;
            });
          } else if (!n.leida) {
            div.addEventListener('click', () => marcarLeida(n.id));
          }
          list.appendChild(div);
        });
      })
      .catch((err) => {
        loading.style.display = 'block';
        loading.textContent = 'Error al cargar';
      });
  }

  function marcarLeida(id) {
    fetch(`${API_BASE}/${id}/leer`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'same-origin'
    })
      .then(r => {
        if (!r.ok) throw new Error('HTTP ' + r.status);
        return r.json();
      })
      .then(data => {
        const badge = document.getElementById('notif-badge');
        if (badge) {
          badge.textContent = data.totalNoLeidas > 99 ? '99+' : data.totalNoLeidas;
          badge.style.display = data.totalNoLeidas > 0 ? 'flex' : 'none';
        }
        const item = document.querySelector(`.notif-item[data-id="${id}"]`);
        if (item) item.classList.remove('notif-no-leida');
      })
      .catch(() => {});
  }

  function marcarTodasLeidas() {
    fetch(`${API_BASE}/leer-todas`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'same-origin'
    })
      .then(r => {
        if (!r.ok) throw new Error('HTTP ' + r.status);
        return r.json();
      })
      .then(data => {
        const badge = document.getElementById('notif-badge');
        if (badge) {
          badge.textContent = '0';
          badge.style.display = 'none';
        }
        document.querySelectorAll('.notif-item').forEach(el => el.classList.remove('notif-no-leida'));
      })
      .catch(() => {});
  }

  function formatFecha(s) {
    if (!s) return '';
    try {
      const d = new Date(s);
      const now = new Date();
      const diff = now - d;
      if (diff < 60000) return 'Ahora';
      if (diff < 3600000) return 'Hace ' + Math.floor(diff / 60000) + ' min';
      if (diff < 86400000) return 'Hace ' + Math.floor(diff / 3600000) + ' h';
      return d.toLocaleDateString('es');
    } catch (e) { return s; }
  }

  function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
  }

  function init() {
    const btn = document.getElementById('btn-notif');
    const dropdown = document.getElementById('notif-dropdown');
    const leerTodas = document.getElementById('notif-leer-todas');

    if (!btn || !dropdown) return;

    btn.addEventListener('click', (e) => {
      e.stopPropagation();
      dropdown.classList.toggle('show');
      if (dropdown.classList.contains('show')) loadNotifications();
    });

    if (leerTodas) {
      leerTodas.addEventListener('click', (e) => {
        e.stopPropagation();
        marcarTodasLeidas();
      });
    }

    document.addEventListener('click', () => dropdown.classList.remove('show'));
    dropdown.addEventListener('click', e => e.stopPropagation());

    loadNotifications();
    setInterval(loadNotifications, 60000);
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
  } else {
    init();
  }
})();
