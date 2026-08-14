(() => {
  const menu = document.querySelector('.side-menu');
  const opener = document.querySelector('[aria-controls="side-menu"]');
  if (!menu || !opener) return;
  const backdrop = document.querySelector('.menu-backdrop');
  const setOpen = open => {
    menu.classList.toggle('open', open);
    backdrop?.classList.toggle('open', open);
    menu.setAttribute('aria-hidden', String(!open));
    opener.setAttribute('aria-expanded', String(open));
    document.body.classList.toggle('menu-open', open);
  };
  opener.addEventListener('click', () => setOpen(!menu.classList.contains('open')));
  document.querySelectorAll('[data-menu-close]').forEach(el => el.addEventListener('click', () => setOpen(false)));
  document.addEventListener('keydown', event => { if (event.key === 'Escape') setOpen(false); });
})();
