/**
 * Sistema de mensajes toast unificado para AgroSoft.
 * Uso: showToast('Mensaje', 'success' | 'error' | 'info');
 */
(function () {
  'use strict';

  var CONTAINER_ID = 'toast-container';
  var DURATION_MS = 5000;
  var VERDE = '#2F5D39';
  var ROJO = '#dc3545';
  var AZUL = '#0d6efd';

  function ensureContainer() {
    var container = document.getElementById(CONTAINER_ID);
    if (container) return container;
    container = document.createElement('div');
    container.id = CONTAINER_ID;
    container.setAttribute('aria-live', 'polite');
    container.setAttribute('aria-atomic', 'true');
    container.style.cssText = 'position:fixed;top:20px;right:20px;z-index:9999;display:flex;flex-direction:column;gap:10px;max-width:360px;pointer-events:none;';
    document.body.appendChild(container);
    injectStyles();
    return container;
  }

  function injectStyles() {
    if (document.getElementById('toast-styles')) return;
    var style = document.createElement('style');
    style.id = 'toast-styles';
    style.textContent =
      '#toast-container .toast-agrosoft{' +
      'font-family:Inter,Segoe UI,sans-serif;padding:14px 18px;border-radius:12px;box-shadow:0 8px 24px rgba(0,0,0,0.12);' +
      'display:flex;align-items:center;gap:10px;animation:toast-in 0.3s ease;pointer-events:auto;' +
      'border:1px solid rgba(0,0,0,0.06);}' +
      '#toast-container .toast-agrosoft.success{background:#e8f5f0;color:#1e4620;border-color:rgba(47,93,57,0.25);}' +
      '#toast-container .toast-agrosoft.error{background:#f8d7da;color:#721c24;border-color:rgba(220,53,69,0.25);}' +
      '#toast-container .toast-agrosoft.info{background:#e7f1ff;color:#0d6efd;border-color:rgba(13,110,253,0.25);}' +
      '@keyframes toast-in{from{transform:translateX(100%);opacity:0;}to{transform:translateX(0);opacity:1;}}';
    document.head.appendChild(style);
  }

  function showToast(message, type) {
    type = type === 'success' || type === 'error' || type === 'info' ? type : 'info';
    var container = ensureContainer();
    var toast = document.createElement('div');
    toast.className = 'toast-agrosoft ' + type;
    toast.setAttribute('role', 'alert');
    var icon = type === 'success' ? '✓' : type === 'error' ? '✗' : 'ℹ';
    toast.innerHTML = '<span style="font-weight:700;">' + icon + '</span><span>' + escapeHtml(String(message)) + '</span>';
    container.appendChild(toast);
    setTimeout(function () {
      if (toast.parentNode) {
        toast.style.animation = 'toast-in 0.2s ease reverse';
        setTimeout(function () {
          if (toast.parentNode) toast.parentNode.removeChild(toast);
        }, 220);
      }
    }, DURATION_MS);
  }

  function escapeHtml(text) {
    var div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
  }

  window.showToast = showToast;
})();
