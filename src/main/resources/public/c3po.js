var fileTxt = "";

/**
 * Log the uploaded file to the console and call /c3po/intercept REST API
 * @param {event} Event The file loaded event
 */
function setAction () {
    var file = document.getElementById('file').files[0];
    var reader = new FileReader();

    reader.addEventListener("load", () => {
        fileTxt = reader.result;

        return callApi();
}, false);
    reader.readAsText(file);

    return;
}

async function callApi () {

    var jsonObj = JSON.parse(fileTxt);

    var jsonTxt = JSON.stringify(jsonObj);

    console.log("sending: " + jsonTxt);

    const response = await fetch(
        "http://localhost:8080/c3po/intercept",
        {
            headers: { "Content-Type": "application/json" },
            method: "POST",
            body: jsonTxt
        }
    );

    const responseText = await response.text();

    //TODO: visualize result on webpage
    // console.log("received!");
    // document.getElementById("result").innerHTML = responseValue;



}