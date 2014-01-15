// Adiciona a função trim() para as Strings.
String.prototype.trim = function(){return this.replace(/^\s+|\s+$/g,"");}
var ID_MAT = 0;
var MAT = 1;
var NOME = 2;
var DIA = 3;
var MES = 4;
var NUM_FALTAS = 5;
var ID_FREQ = 6;
var ALTERADA = 7;
var NUM_AULAS = 8;
var ID_DISCENTE = 9;
var DATA = 10;
var TRANCADO = 11;

// Adiciona um seletor de expressões regulares ao jQuery.
J.expr[':'].regex = function(elem, index, match) {
    var matchParams = match[3].split(',');
    var validLabels = /^(data|css):/;
    var attr = {
            method: matchParams[0].match(validLabels) ? 
                        matchParams[0].split(':')[0] : 'attr',
            property: matchParams.shift().replace(validLabels,'')
        };
    var regexFlags = 'ig';
    var regex = new RegExp(matchParams.join('').replace(/^\s+|\s+$/g,''), regexFlags);
    
    return regex.test(J(elem)[attr.method](attr.property));
};

function escondeDialog (){
	J("#dialog").css("display","none");
	J("#mascara").css("display","none");
}

/**
 * Converte a string passada para um vetor.
 * A string passada deve ser no formato "1,2,3;4,5,6;7,8,9"
 */
function converteEmVetor(string){
	var vetor = new Array();
	
	var itens = string.split(";");

	for (var i = 0; i < itens.length; i++){
		var item = new Array ();
		var auxItem = itens[i].split(",");
		for (var j = 0; j < auxItem.length; j++)
			item[item.length] = auxItem[j];

		vetor[vetor.length] = item;
	}

	return vetor;
}

/**
 * Converte o vetor passado na string que o representa.
 */
function converteEmString (vetor){
	var string = "";
	
	for (var i = 0; i < vetor.length; i++)
		string += (string == "" ? "" : ",")  + vetor[i];

	return string;
}

/**
 * Gera a planilha
 */
function inicializa (){
	var tabela = document.createElement("table");
	tabela.id = "planilha";

	// Cabeçalho e corpo da tabela.
	var tbody = document.createElement("tbody");
	var thead = document.createElement("thead");

	var indiceAluno = 0;
	var indiceAula = 0;
	
	var idMatriculaAtual = "-1";
	var mesAtual = -1;

	var linha = document.createElement("tr");
	var linhaMeses = document.createElement("tr");
	
	var auxTH = document.createElement("th");
	linhaMeses.appendChild(auxTH);
	
	auxTH = document.createElement("th");
	auxTH.appendChild(document.createTextNode("#"));
	linha.appendChild(auxTH);
	
	auxTH = document.createElement("th");
	// Unicode para funcionar no IE
	auxTH.appendChild(document.createTextNode("Matr\u00edcula"));
	auxTH.style.cssText = "text-align:center;border-bottom:2px solid #333333;";
	linha.appendChild(auxTH);
	
	auxTH = document.createElement("th");
	linhaMeses.appendChild(auxTH);
	

	auxTH = document.createElement("th");
	auxTH.appendChild(document.createTextNode("Nome"));
	auxTH.style.cssText = "text-align:left;border-bottom:2px solid #333333;width:200px;";
	linha.appendChild(auxTH);

	auxTH = document.createElement("th");
	linhaMeses.appendChild(auxTH);
		
	var auxTH2 = null;
	var contadorDias = 0;
	// Para cada dia de aula, adiciona um th com o número.
	for (var i = 0; i < aulas.length; i++){

		if (aulas[i][1] != mesAtual){
			if (auxTH2 != null){
				auxTH2.colSpan = contadorDias;

				auxTH2.appendChild(document.createTextNode(getNomeMes(mesAtual)));
				
				linhaMeses.appendChild(auxTH2);
				contadorDias = 0;
			}
			
			auxTH2 = document.createElement("th");
			mesAtual = aulas[i][1];
		}								

		auxTH = document.createElement("th");
		auxTH.className = i;

		auxTH.style.cssText = "border-bottom:2px solid #333333;cursor:pointer;" + (mesAtual != aulas[i][1] ? "border-left:2px solid #333333;" : "" );
		// Feriado. Deixa vermelho.
		if (aulas[i][5] == "true")
			auxTH.className += " feriado";
		else if ( aulas[i][6] == "true" )
			auxTH.className += " cancelada";
		else  {
			// Não conta mais as aulas. Utiliza a carga horária do detalhe do componente da turma
			// Conta quantas aulas tem no semestre.
			// totalAulas += parseInt(aulas[i][2]);
			
			// O professor já lançou a frequência para este dia, deixa verde
			if (aulas[i][4] == "true")
				auxTH.className += " lancada";
			// Dia de aula normal, adiciona a funcionalidade de dar presença para todos.
			else
				auxTH.onclick = function (){zerarDia(this);};
		}
		
		auxTH.appendChild(document.createTextNode(aulas[i][0]));
		linha.appendChild(auxTH);
		contadorDias ++;
	}

	if (auxTH2 != null){
		auxTH2.colSpan = contadorDias;

		auxTH2.appendChild(document.createTextNode(getNomeMes(mesAtual)));
		
		linhaMeses.appendChild(auxTH2);
	}

	// Adiciona os headers dos totais.
	auxTH = document.createElement("th");
	auxTH.appendChild(document.createTextNode("Total"));
	auxTH.style.cssText = "width:65px;";
	auxTH.colSpan = 2;
	linhaMeses.appendChild(auxTH);

	auxTH = document.createElement("th");
	auxTH.appendChild(document.createTextNode("Qtd"));
	auxTH.style.cssText = "text-align:right;width:30px;";
	linha.appendChild(auxTH);

	auxTH = document.createElement("th");
	auxTH.appendChild(document.createTextNode("%"));
	auxTH.style.cssText = "text-align:right;width:35px;";
	linha.appendChild(auxTH);

	thead.appendChild(linhaMeses);
	thead.appendChild(linha);

	linha = null;

	var auxFaltas = 0;

	// Para todos os alunos, cria as linhas da tabela.
	for (var i = 0; i < alunos.length; i++){

		// Se mudou de aluno, adiciona a linha à tabela e cria uma nova linha.
		if (alunos[i][ID_MAT] != idMatriculaAtual){
			indiceAluno++;
			indiceAula = 0;
			// Se não está começando agora, adiciona o aluno anterior.
			if (linha != null){

				var percentualFaltas = auxFaltas*100/totalAulas;
				if (percentualFaltas > 100)
					percentualFaltas = 100;

				var total = document.createElement("td");
				total.appendChild(document.createTextNode(auxFaltas));
				total.className = "celula total";
				total.style.cssText = "text-align:right;";
				linha.appendChild(total);

				total = document.createElement("td");
				total.appendChild(document.createTextNode(corrigirFloat(""+percentualFaltas)));
				total.className = "celula percentualTotal";
				total.style.cssText = "text-align:right;";
				linha.appendChild(total);

				if (percentualFaltas > (100-FREQUENCIA_MINIMA))
					linha.className += " reprovadoFalta";
				
				tbody.appendChild(linha);

				auxFaltas = 0;
			}

			linha = document.createElement("tr");
			linha.className = "linha" + (indiceAluno % 2 == 0 ? "Par" : "Impar");

			var numero = document.createElement("td");
			// Adiciona a matrícula e o nome do aluno.
			var matricula = document.createElement("td");
			var nome = document.createElement("td");

			numero.className="celula";
			matricula.className="celula";
			nome.className="celula";

			numero.appendChild(document.createTextNode(indiceAluno));
			numero.style.cssText = "text-align:center;font-weight:normal;";
			
			matricula.appendChild(document.createTextNode(alunos[i][MAT]));
			matricula.style.cssText = "text-align:center;font-weight:normal;";

			var divNome = document.createElement("div");
			divNome.style.cssText = "text-align:left;font-weight:normal;overflow:hidden;width:200px;height:15px;";
			divNome.appendChild(document.createTextNode(alunos[i][NOME]));
			nome.appendChild(divNome);
			
			linha.appendChild(numero)
			linha.appendChild(matricula);
			linha.appendChild(nome);
		}

		// Indica qual o aluno atual.
		idMatriculaAtual = alunos[i][ID_MAT];

		// Essa condição só deve ocorrer caso o número de aulas supere o número de alunos
		if ( indiceAula < aulas.length ) {
		
			var celula = document.createElement("td");
			celula.className = "celula aluno_" + idMatriculaAtual + " aula_" + indiceAula;
			var css = "";
	
			// Verifica se mudou o mês e adiciona maior destaque à celula.
			if (mesAtual != aulas[indiceAula][1])
				css += "border-left:2px solid #000000;";
	
			mesAtual = aulas[indiceAula][1];
	
			// Se a aula atual for um feriado, exibe um "F";
			if (aulas[indiceAula][5] == "true"){
				css += "background:#FFEEEE;";
			// Se o o professor tiver cancelado a aula daquele dia;
			} else if (aulas[indiceAula][6] == "true"){
				css += "background:#FFFF99;";
				// Se o aluno trancou, exibe um "T";
			} else if (alunos[i][TRANCADO] == "true"){
				celula.appendChild(document.createTextNode("T"));
				css += "color:#AAAAAA;";
			// Senão, exibe a frequência do aluno.
			} else {
				celula.appendChild(document.createTextNode(alunos[i][NUM_FALTAS] == "null" ? "" : alunos[i][NUM_FALTAS]));
				celula.onclick = function (){mudarValor(this); exibirInput(this);};
			}
			celula.style.cssText = css;
			linha.appendChild(celula);
	
			indiceAula++;
	
			if (alunos[i][NUM_FALTAS] != null && alunos[i][NUM_FALTAS] != "null" && alunos[i][NUM_FALTAS] != "T")
				auxFaltas += parseInt(alunos[i][NUM_FALTAS]);
		}
	}

	// Adiciona o último aluno.
	var percentualFaltas = auxFaltas*100/totalAulas;
	if (percentualFaltas > 100)
		percentualFaltas = 100;
				
	var total = document.createElement("td");
	total.appendChild(document.createTextNode(auxFaltas));
	total.className = "celula total";
	total.style.cssText = "text-align:right;";
	linha.appendChild(total);

	total = document.createElement("td");
	total.appendChild(document.createTextNode(corrigirFloat(""+percentualFaltas)));
	total.className = "celula percentualTotal";
	total.style.cssText = "text-align:right;";
	linha.appendChild(total);

	if (percentualFaltas > 100-FREQUENCIA_MINIMA)
		linha.className += " reprovadoFalta";
	
	linha.appendChild(total);

	tbody.appendChild(linha);

	// Finaliza a tabela.
	tabela.appendChild(thead);
	tabela.appendChild(tbody);

	// Adiciona a tabela à div base.
	document.getElementById("basePlanilha").appendChild(tabela);

	escondeDialog();
}

function getNomeMes (mesAtual){
	switch (mesAtual){
		case "1": return "Janeiro";
		case "2": return "Fevereiro";
		case "3": return "Março";
		case "4": return "Abril";
		case "5": return "Maio";
		case "6": return "Junho";
		case "7": return "Julho";
		case "8": return "Agosto";
		case "9": return "Setembro";
		case "10": return "Outubro";
		case "11": return "Novembro";
		case "12": return "Dezembro";
	}
}

function exibirInput (td){
	// Só cria um novo input se a célula só tiver texto
	if (td.firstChild.nodeType == 3){

		// Descobre a quantidade de aulas para o dia clicado.
		var aulasDia = td.className;
		aulasDia = aulasDia.split(aulasDia.indexOf(",") >= 0 ? "," : " ");
		aulasDia = aulasDia[2].split("_");
		aula = aulasDia[1];
		aulasDia = aulas[aula][2];

		var hoje = new Date();
		var diaClicado = aulas[aula][0];
		var mesClicado = aulas[aula][1];
		var diaAtual = hoje.getDate();
		var mesAtual = hoje.getMonth()+1;
		
		if ( (mesClicado > mesAtual) || (mesClicado == mesAtual && diaClicado > diaAtual) ) {
				return;
			}
		
		var valorAtual = td.innerHTML;
		
		while (td.childNodes.length > 0)
			td.removeChild(td.childNodes[0]);

		var input = document.createElement("input");
		input.value = valorAtual;
		input.style.cssText = "border:1px solid #FFAA00;background:#FFFFFF;";
		input.maxLength = 2;

		var fi = function (){
			return formatarInteiro(this);
		}
		input.onkeyup = fi;
		
		input.onblur = function (){

			// Descobre a quantidade de aulas para o dia clicado.
			var pai = this.parentNode;
			var aulasDia = pai.className;
			aulasDia = aulasDia.split(aulasDia.indexOf(",") >= 0 ? "," : " ");
			aulasDia = aulasDia[2].split("_");
			aula = aulasDia[1];
			aulasDia = aulas[aula][2];
			
			if (this.value == null)
				this.value = "";
			else if (parseInt(this.value) > parseInt(aulasDia))
				this.value = aulasDia;
			else if  (parseInt(this.value) < 0)
				this.value = 0;
			
			var valorInput = this.value;
			var pai = this.parentNode;
			pai.removeChild(pai.childNodes[0]);
			pai.appendChild(document.createTextNode(valorInput));

			calcularFaltas(J(pai.parentNode));
		};
		
		td.appendChild(input);
		
		input.focus();
		input.select();
	}
}

function zerarDia(th){
	var aula = th.className;

	var hoje = new Date();
	var dataClicada = aulas[aula][3];
	// Cria objeto Date para a data clicada
	var array = dataClicada.split(" ");
	var dataString = array[1] + " " + array[2] + ", " + array[5];
	var data = new Date(dataString);
	
	if ( (data.getTime() > hoje.getTime()) ) {
			alert("Não é possível cadastrar frequências para datas posteriores à atual.");
			return;
		}
	
	J(".aula_"+aula).each(function (){
		var celula = J(this);
		var texto = celula.html();

		if (texto.trim() == "")
			celula.html("0");
	});
}

function mudarValor(td){

	var celula = J(td);
	var aluno = celula.parent();
	var aula;

	// Descobre a quantidade de aulas para o dia clicado.
	var aulasDia = celula.attr("className");
	aulasDia = aulasDia.split(aulasDia.indexOf(",") >= 0 ? "," : " ");
	aulasDia = aulasDia[2].split("_");
	aula = aulasDia[1];
	aulasDia = aulas[aula][2];

	var hoje = new Date();
	var dataClicada = aulas[aula][3];
	// Cria objeto Date para a data clicada
	var array = dataClicada.split(" ");
	var dataString = array[1] + " " + array[2] + ", " + array[5];
	var data = new Date(dataString);
	
	if ( (data.getTime() > hoje.getTime()) ) {
			alert("Não é possível cadastrar frequências para datas posteriores à atual.");
			return;
		}
	
	// Muda o valor do dia clicado.
	if (celula.html() == "")
		celula.html(aulasDia);
	else if (celula.attr("firstChild").nodeType == 1){
		var input = celula.attr("firstChild");
		if (input.value == "")
			input.value = aulasDia;
		else
			input.value = parseInt(input.value) - 1;
		
		if (parseInt(input.value) < 0)
			input.value = aulasDia;
	} else {
		celula.html(""+(parseInt(celula.html())-1));
		if (parseInt(celula.html()) < 0)
			celula.html(aulasDia);
	}

	// Zera o restante do dia que ainda não foi preenchido.
	J(".aula_"+aula).each(function (){
		var celula = J(this);

		if (celula.attr("firstChild").nodeType == 3){
			var texto = celula.html();

			if (texto.trim() == "")
				celula.html("0");
		}
	});

	// Calcula as faltas do aluno.
	calcularFaltas(aluno);
}

function calcularFaltas (aluno){

	var faltas = 0;
	
	J("td:regex(class, aluno_\\d+)", aluno).each(function (){
		var td = J(this);
		var valor = "";

		if (td.attr("firstChild") != null){
			if (td.attr("firstChild").nodeType == 3)
				valor = td.html();
			else
				valor = td.attr("firstChild").value;
		}

		if (valor != "")
			faltas += parseInt(valor);
	});

	// Exibe as faltas do aluno
	J(".total", aluno).html(faltas);

	// Calcula o percentual de faltas do aluno.
	var percentualFaltas = faltas * 100 / totalAulas;
	if (percentualFaltas > 100)
		percentualFaltas = 100;

	// Exibe o percentual de faltas do aluno.
	J(".percentualTotal", aluno).html(corrigirFloat(""+percentualFaltas));

	// Se o aluno estiver reprovado por falta, exibe em vermelho.
	if (faltas > totalAulas * (100 - FREQUENCIA_MINIMA) / 100)
		aluno.addClass("reprovadoFalta");
	else
		aluno.removeClass("reprovadoFalta");
}

function corrigirFloat(valor){
	var regExp = /^\d{1,3}(\.\d{1,2})?/;
	var resultado = valor.match(regExp);

	if (resultado != null)
		return resultado[0];

	return 0;
	
}

/**
 * Varre toda a planilha e configura as frequências para serem enviadas ao servidor.
 */
function atualizarFrequencias (){
	var idMatriculaAtual = "";
	var frequenciasString = "";

	for (var i = 0; i < alunos.length; i++){
		if (alunos[i][ID_MAT] != idMatriculaAtual){
			idMatriculaAtual = alunos[i][ID_MAT];

			var aux = "";
			var test = J(".aluno_"+idMatriculaAtual);
			J(".aluno_"+idMatriculaAtual).each(function (){
				var valorAtual = J(this).text();
				
				if (valorAtual != ""){
					if (alunos[i][NUM_FALTAS] != valorAtual)
						alunos[i][NUM_FALTAS] = valorAtual;
				}
				
				var frequenciaAtualString = converteEmString(alunos[i]);

				frequenciasString += (frequenciasString == "" ? "" : ";") + frequenciaAtualString;

				i++;
			});
		}

		i --;
	}

	document.getElementById("form:frequencias").value = frequenciasString;
}