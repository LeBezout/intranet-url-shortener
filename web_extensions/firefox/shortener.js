const APPLICABLE_PROTOCOLS = ["http:", "https:"];
/*
 * Returns true only if the URL's protocol is in APPLICABLE_PROTOCOLS.
 * source : https://github.com/mdn/webextensions-examples/blob/master/apply-css/background.js
 */
function protocolIsApplicable(url) {
  var anchor =  document.createElement('a');
  anchor.href = url;
  return APPLICABLE_PROTOCOLS.includes(anchor.protocol);
}

/*
 * the listener
 */
function shorten(tab) {
  var gettingActiveTab = browser.tabs.query({active: true, currentWindow: true});
  gettingActiveTab.then((tabs) => {
    console.log(tabs[0].url);
    // TODO shorten this URL
  });

}
/*
Initialize the page action: set icon and title, then show.
Only operates on tabs whose URL's protocol is applicable.
*/
function initializePageAction(tab) {
  if (protocolIsApplicable(tab.url)) {
    browser.pageAction.setIcon({tabId: tab.id, path: "icons/url_shortener_ico-32.png"});
    //browser.pageAction.setTitle({tabId: tab.id, title: "???"});
    browser.pageAction.show(tab.id);
  } else {
    console.log(tab.url + " : not applicable");
  }
}

/*
When first loaded, initialize the page action for all tabs.
*/
var gettingAllTabs = browser.tabs.query({});
gettingAllTabs.then((tabs) => {
  for (let tab of tabs) {
    initializePageAction(tab);
  }
});

/*
Each time a tab is updated, reset the page action for that tab.
*/
browser.tabs.onUpdated.addListener((id, changeInfo, tab) => {
  initializePageAction(tab);
});

/*
 * add the listener
 */
browser.pageAction.onClicked.addListener(shorten);

//const currentURL = window.document.location.href;
//console.log(currentURL);
console.log("shortener is loaded");
