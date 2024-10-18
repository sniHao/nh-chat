/**
 * theme 主题色 true为暗黑模式 false为明亮模式
 */
export default (theme: boolean = true) => {
  const elementsToStyle = [
    { selector: '.home-bg-color', default: { backgroundColor: '#0f172a' }, lightness: { backgroundColor: 'white' } },
    { selector: '.ft-color-tips-4', default: { color: 'rgb(240, 240, 240)' }, lightness: { color: 'rgb(40, 40, 40)' } },
    { selector: '.ft-color-tips-3', default: { color: 'rgb(230, 230, 230)' }, lightness: { color: 'rgb(50, 50, 50)' } },
    { selector: '.ft-color-tips-2', default: { color: 'rgb(220, 220, 220)' }, lightness: { color: 'rgb(20, 20, 20)' } },
    { selector: '.ft-color-tips', default: { color: 'rgb(210, 210, 210)' }, lightness: { color: 'rgb(80, 80, 80)' } },
    { selector: '.ft-color-white', default: { color: 'white' }, lightness: { color: 'black' } },
    { selector: '.user-main', default: { color: 'rgb(210, 210, 210)' }, lightness: { color: 'rgb(80, 80, 80)' } },
    // { selector: '.cbb-tips', default: { color: 'rgb(210, 210, 210)' }, lightness: { color: 'rgb(80, 80, 80)' } },
    // { selector: '.n-timeline-item-content__content', default: { color: 'rgb(210, 210, 210)' }, lightness: { color: 'rgb(80, 80, 80)' } },
    { selector: '.main-nav', default: { backgroundColor: 'rgba(15, 23, 42, 0.6)' }, lightness: { backgroundColor: 'rgba(255, 255, 255, 0.6)' } },
    { selector: '.chat-main', default: { backgroundColor: 'rgba(255, 255, 255, 0.12)' }, lightness: { backgroundColor: 'rgb(209 209 209 / 12%)' } },
    { selector: '.cb-input .cb-input-main', default: { backgroundColor: '#2c3344' }, lightness: { backgroundColor: '#fafafa' } },
    { selector: '.cb-input .cb-input-main .n-input__textarea-el', default: { color: 'white' }, lightness: { color: 'black' } }
  ];

  elementsToStyle.forEach((item) => {
    const elements = document.querySelectorAll(item.selector) as unknown as HTMLElement[];
    elements.forEach((ite) => {
      Object.assign(ite.style, theme ? item.default : item.lightness);
    });
  });
};
