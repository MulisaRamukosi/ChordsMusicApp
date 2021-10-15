javascript:(() => {

    const searchIn = document.querySelector('input[name=q]');
    const searchBtn = document.querySelector('button[type=submit]');

    searchIn.value = `%s`;
    searchBtn.click();

})()
