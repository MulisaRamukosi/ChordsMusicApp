javascript:(() => {

    const mp3DownloadPath = document.evaluate(`//a[contains(., 'MP3')]`, document, null, XPathResult.ANY_TYPE, null);
    const mp3DownloadBtn = mp3DownloadPath.iterateNext();
    mp3DownloadBtn.click();
})()
