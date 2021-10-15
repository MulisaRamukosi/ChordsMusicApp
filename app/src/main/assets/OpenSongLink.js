javascript:(() => {

    let track = 0;

    const musicCheck = setInterval(() => {
        track += 200;
        const music = document.getElementsByClassName('media-body');
        if(music != null && typeof music !== 'undefined'){
            clearInterval(musicCheck);
            const musicLink = music[0].childNodes[1];
            musicLink.click();
        }
        else if(track >= 10000) clearInterval(musicCheck);
    }, 200);


})()