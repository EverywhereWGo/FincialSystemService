/**
 * 获取设备信息
 */
export function getDeviceInfo() {
  const ua = navigator.userAgent;
  const browser = getBrowser(ua);
  const os = getOS(ua);
  const device = getDevice(ua);
  
  return JSON.stringify({
    browser,
    os,
    device,
    screenSize: `${window.screen.width}x${window.screen.height}`,
    userAgent: ua
  });
}

/**
 * 获取浏览器信息
 */
function getBrowser(ua) {
  let browserName = "未知浏览器";
  let browserVersion = "";
  
  // Chrome
  if (ua.indexOf("Chrome") > -1) {
    browserName = "Chrome";
    browserVersion = ua.match(/Chrome\/([\d.]+)/)[1];
  }
  // Firefox
  else if (ua.indexOf("Firefox") > -1) {
    browserName = "Firefox";
    browserVersion = ua.match(/Firefox\/([\d.]+)/)[1];
  }
  // Edge
  else if (ua.indexOf("Edg") > -1) {
    browserName = "Edge";
    browserVersion = ua.match(/Edg\/([\d.]+)/)[1];
  }
  // IE
  else if (ua.indexOf("MSIE") > -1 || ua.indexOf("Trident") > -1) {
    browserName = "IE";
    browserVersion = ua.indexOf("MSIE") > -1 ? 
      ua.match(/MSIE ([\d.]+)/)[1] : "11.0";
  }
  // Safari
  else if (ua.indexOf("Safari") > -1 && ua.indexOf("Chrome") === -1) {
    browserName = "Safari";
    browserVersion = ua.match(/Version\/([\d.]+)/)[1];
  }
  
  return `${browserName} ${browserVersion}`;
}

/**
 * 获取操作系统信息
 */
function getOS(ua) {
  let osName = "未知操作系统";
  let osVersion = "";
  
  // Windows
  if (ua.indexOf("Windows") > -1) {
    osName = "Windows";
    if (ua.indexOf("Windows NT 10.0") > -1) osVersion = "10";
    else if (ua.indexOf("Windows NT 6.3") > -1) osVersion = "8.1";
    else if (ua.indexOf("Windows NT 6.2") > -1) osVersion = "8";
    else if (ua.indexOf("Windows NT 6.1") > -1) osVersion = "7";
    else if (ua.indexOf("Windows NT 6.0") > -1) osVersion = "Vista";
    else if (ua.indexOf("Windows NT 5.1") > -1) osVersion = "XP";
    else osVersion = "其他";
  }
  // Mac
  else if (ua.indexOf("Mac") > -1) {
    osName = "Mac OS";
    osVersion = ua.match(/Mac OS X ([0-9_]+)/)[1].replace(/_/g, ".");
  }
  // Linux
  else if (ua.indexOf("Linux") > -1) {
    osName = "Linux";
  }
  // Android
  else if (ua.indexOf("Android") > -1) {
    osName = "Android";
    osVersion = ua.match(/Android ([\d.]+)/)[1];
  }
  // iOS
  else if (ua.indexOf("iPhone") > -1 || ua.indexOf("iPad") > -1) {
    osName = "iOS";
    osVersion = ua.match(/OS ([\d_]+)/)[1].replace(/_/g, ".");
  }
  
  return `${osName} ${osVersion}`;
}

/**
 * 获取设备类型
 */
function getDevice(ua) {
  if (ua.indexOf("iPhone") > -1) {
    return "iPhone";
  } else if (ua.indexOf("iPad") > -1) {
    return "iPad";
  } else if (ua.indexOf("Android") > -1 && ua.indexOf("Mobile") > -1) {
    return "Android Phone";
  } else if (ua.indexOf("Android") > -1) {
    return "Android Tablet";
  } else {
    return "Desktop";
  }
}