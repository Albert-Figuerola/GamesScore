$(document).ready(function () {
    /*
     * MATERIAZLIZE
     */
    $('ul.tabs').tabs('select_tab', 'test4');
    $('.chips').material_chip();
    $('.chips-initial').material_chip({
        data: [{tag: 'Apple'}, {tag: 'Microsoft'}, {tag: 'Google'}]
    });
    $('.chips-placeholder').material_chip({
        placeholder: 'Enter a tag',
        secondaryPlaceholder: '+Tag'
    });
    $('.chips').on('chip.add', function (e, chip) {
        alert("on add");
        alert(chip.tag);
        $('.chips-initial').material_chip('data').forEach(function (untag) {
            alert(untag.tag);
        });
    });

    $('.chips').on('chip.delete', function (e, chip) {
        alert("on delete");
    });

    $('.chips').on('chip.select', function (e, chip) {
        alert("on select");
    });
    $(".button-collapse").sideNav();
    $('.modal-trigger').leanModal();
    /*
     * END MATERIALIZE
     */


    var jocs = $("#jocs");
    var servletURL = "Biblioteca?action=listJocs";
    $.ajax({
        type: "GET",
        crossDomain: true,
        dataType: "json",
        async: true,
        url: servletURL,
        success: function (data) {
            var myHtml = renderListJocs(data);
            jocs.html(myHtml);
            var dades = calculTotalsLlista(data);
            $("#total-puntuacions").text(dades[0]);
            $("#mitjana-puntuacions").text(dades[1]);
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.info('in error');
            console.log(jqXHR, textStatus, errorThrown);
            alert("You can not send Cross Domain AJAX requests: " + errorThrown);
        }
    });

  //p3//
    var servletURL = "user?action=formUser";
    $.ajax({
        type: "GET",
        crossDomain: true,
        dataType: "json",
        async: true,
        url: servletURL,
        success: function (data) {
            $("#username").html("<h5>Puntuacions: " + data.user +'</h5>');
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.info('in error');
            console.log(jqXHR, textStatus, errorThrown);
        }
    });


});
$(document).on('click', '[class*="puntuacio"]', function () {
    var jocPuntuacio = $(this).attr("id").split("-");
    var joc = jocPuntuacio[0];
    var puntuacio = jocPuntuacio[1];
    var afegir = $(this.parentElement);
    var servletURL = "Biblioteca?action=addJocPuntuacions&joc=" + joc + "&puntuacio=" + puntuacio;
    $.ajax({
        type: "GET",
        crossDomain: true,
        dataType: "json",
        async: true,
        url: servletURL,
        success: function (data) {
            
            afegir.hide();
            $("#check-" + data.jocPuntuat).show();
            $("#puntuacio-" + data.jocPuntuat).text(data.puntuacioJoc);

            calculTotals(data);
            

        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.info('in error');
            console.log(jqXHR, textStatus, errorThrown);
            alert("You can not send Cross Domain AJAX requests: " + errorThrown);
        }
    });
});



function renderListJocs(data) {
    var myHtml = "";
    $.each(data.jsonArray, function (index) {
        myHtml += '<div class="col s12 m3 l3"> <div class="card grey lighten-4 hoverable">';
        myHtml += renderJoc(data.jsonArray[index]);
        myHtml += '</div></div>';
    });
    return myHtml;
}

function renderJoc(dataJoc) {
    var myHtmlP = "";
    var joc = "";
    var puntuacio = 0.0;
    var afegit = false;
    $.each(dataJoc, function (key, value) {
        if (key == 'name') {
            joc = value;
        }
        if (key == 'puntuacio') {
            puntuacio = parseFloat(value);
        }
        if (key == 'afegit') {
            if (value == 'SI') {
                afegit = true;
            } else {
                afegit = false;
            }
        }
    });
    myHtmlP += '<div class="card-image"><img src="img/' + joc + '.jpg"/><span class="card-title">' + joc + '</span></div>';
    myHtmlP += '<div class="chip"><h6>Puntuació: <span id="puntuacio-' + joc + '">' + puntuacio + '</h6></div>';
    
    if (afegit) {
        myHtmlP += '<div class="card-action right-align">';
        myHtmlP += '<img id ="check-' + joc + '" style="width: 10px;" src="img/check.png"/></div></div>';

    } else {
        myHtmlP += '<div class="card-action right-align"><div>';
       for (var i = 1; i <= 5; i++){
        myHtmlP += '<a class ="puntuacio" href="#" id="' + joc + '-' +i+'">'+i+'</a>';
       }
        myHtmlP += '</div><img id ="check-' + joc + '" style="display: none;width: 20px;" src="img/check.png"/></div></div>';
    }

    return myHtmlP;
}


function calculTotalsLlista(data){
    var dades = [0,0.0];
    var total = 0.0;
     $.each(data.jsonArray, function (index) {
        
        var joc = data.jsonArray[index];
        var m = joc["afegit"];
        var puntuacio = joc["puntuacio"];
        if (m == "SI"){
            dades[0] = dades[0] + 1;
            total = total + parseFloat(puntuacio);
        }
     });
    
    if(dades[0] != 0) {
        dades[1] = total / dades[0];
    } else {
        dades[1] = 0.0;
    }
  
    return dades;
}

function calculTotals(data) {
    var mitjana = parseFloat(document.getElementById('mitjana-puntuacions').innerHTML);
    var puntuacions = parseInt(document.getElementById('total-puntuacions').innerHTML);
    $("#total-puntuacions").text(puntuacions + 1);
    $("#mitjana-puntuacions").text(((mitjana * puntuacions) + data.puntuacioJoc ) / (puntuacions + 1)); 
}