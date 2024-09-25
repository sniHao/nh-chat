/**
 * 创建一个节流函数，该函数在指定的时间间隔内最多执行一次。
 *
 * @param {Function} fn - 要节流的函数。
 * @param {number} delay - 节流的时间间隔（毫秒）。
 * @returns {Function} - 返回一个新的函数，该函数在 delay 毫秒内最多执行一次 fn。
 */
export function throttle(fn: (...args: any[]) => void, delay: number): (...args: any[]) => void {
  let lastTime = 0;
  return function (this: any, ...args: any[]) {
    const now = Date.now();
    if (now - lastTime >= delay) {
      lastTime = now;
      fn.apply(this, args);
    }
  };
}
