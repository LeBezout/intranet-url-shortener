// i18n
document.querySelector("#configLabelServerUrl").innerText = browser.i18n.getMessage("configLabelServerUrl");
document.querySelector("#configSaveServerUrl").innerText = browser.i18n.getMessage("configSaveServerUrl");


function saveOptions(e) {
  browser.storage.sync.set({
    shortenerUrl: document.querySelector("#txtShortenerUrl").value
  });
  e.preventDefault();
}

function restoreOptions() {
  var gettingItem = browser.storage.sync.get('shortenerUrl');
  gettingItem.then((res) => {
    document.querySelector("#txtShortenerUrl").value = res.shortenerUrl || '';
  });
}

document.addEventListener('DOMContentLoaded', restoreOptions);
document.querySelector("form").addEventListener("submit", saveOptions);
