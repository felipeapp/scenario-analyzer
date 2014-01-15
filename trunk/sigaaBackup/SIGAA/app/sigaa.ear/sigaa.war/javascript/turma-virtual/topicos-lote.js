// Adiciona a função format() para as datas.
				Date.prototype.format = function (){
					var dia = this.getDate();
					var mes = this.getMonth() + 1;
					var ano = this.getFullYear();
					
					return (dia < 10 ? "0" : "") + dia  + "/" + (mes < 10 ? "0" : "") + mes + "/" + ano;
				};
	
				// Adiciona a função trim() para as Strings.
				String.prototype.trim = function(){return this.replace(/^\s+|\s+$/g,"");}
			
				function TopicoAula (id, titulo, dataInicio, dataFim){
					this.id = id;
					this.titulo = titulo;
					this.dataInicio = dataInicio;
					this.dataFim = dataFim;
					this.associado = false;
					this.modificado = false;
				}
			
				var topicos = new Array ();
				var associacoes = new Array ();
				
				function configurarTopicos(stringTopicos){
					var auxTs = stringTopicos.split("%!%");
	
					for (var i = 0; i < auxTs.length; i++){
						var auxT = auxTs[i].split("%@%");
						topicos[i] = new TopicoAula(auxT[0], auxT[1], auxT[2], auxT[3]);
	
						var tabela = document.getElementById("tbody");
	
						var tr = document.createElement("tr");
						tr.className = "linha" + (i % 2 == 0 ? "Par" : "Impar");
	
						var td = document.createElement("td");		
						td.appendChild(document.createTextNode(topicos[i].dataInicio));
						tr.appendChild(td);
	
						var td = document.createElement("td");
						td.appendChild(document.createTextNode(topicos[i].dataFim));
						tr.appendChild(td);
	
						var td = document.createElement("td");
						if (topicos[i].id == 0 && i > 0){
							var img = document.createElement("img");
							img.src = CTX+"/ava/img/cima_direita.png";
							img.className = "associar " + i;
							img.onclick = function (){ associar(this.className); };
							td.appendChild(img);
	
							img = document.createElement("img");
							img.src = CTX+"/img/delete.png";
							img.style.cssText = "display:none;";
							img.className = "desassociar " + i;
							img.onclick = function (){ desassociar(this.className); };
							td.appendChild(img);
						}
						tr.appendChild(td);
	
						var td = document.createElement("td");
						var input = document.createElement("input");
						input.value = topicos[i].titulo;
						input.className = i;
						input.style.cssText = "width:99%;";
						input.onblur = function (){ atualizarTitulo(this.className); };
						td.appendChild(input);
	
						var span = document.createElement("span");
						span.className = i;
						td.appendChild(span);
						
						tr.appendChild(td);
						
						tabela.appendChild(tr);
						
						topicos[i].titulo = input.value;
						input.value = topicos[i].titulo;
					}
				}
	
				function atualizarTitulo (id){
					topicos[id].titulo = J("input."+id).val();
					topicos[id].modificado = true;
	
					id++;
	
					while (id < topicos.length){
						if (topicos[id].associado){
							topicos[id].titulo = topicos[id-1].titulo;
							J("span."+id).html(topicos[id].titulo);
						} else
							break;
	
						id++;
					}
				}
	
				function associar (classe){
					var aux = classe.indexOf(",") >= 0 ? "," : " ";
					var id = classe.split(aux)[1];
	
					topicos[id].associado = true;
					topicos[id].titulo = topicos[id - 1].titulo;
					topicos[id].modificado = true;
					topicos[id - 1].modificado = true;
	
					J("img."+id).each(function (){if (J(this).attr("className").indexOf("des") == -1) J(this).hide(); else J(this).show();});
	
					var span = J("span."+id);
					span.html(topicos[id].titulo);
					span.show();
					
					J("input."+id).hide();
	
					desabilitarAnterior(id - 1);
				}
	
				function desassociar (classe){
					var aux = classe.indexOf(",") >= 0 ? "," : " ";
					var id = classe.split(aux)[1];
	
					topicos[id].associado = false;
					topicos[id].titulo = "";
					topicos[id].modificado = true;
	
					J("img."+id).each(function (){if (J(this).attr("className").indexOf("des") >= 0) J(this).hide(); else J(this).show();});
					J("span."+id).hide();
					var input = J("input."+id);
					input.val("");
					input.show();
	
					habilitarAnterior(id - 1);
				}
	
				function desabilitarAnterior (id){
					J("img."+id).each(function (){J(this).hide();});
				}
	
				function habilitarAnterior (id){
					J("img."+id).each(function (){
						if (J(this).attr("className").indexOf("des") >= 0){
							if (topicos[id].associado)
								J(this).show();
							else
								J(this).hide();
						} else {
							if (topicos[id].associado)
								J(this).hide();
							else
								J(this).show();
						}
					});
				}
	
				function prepararEnvio (){
	
					var stringTA = "";
					var indiceFinal = -1;
					for (var i = topicos.length -1 ; i >= 0; i--){
						if (topicos[i].titulo.trim() == "")
							continue;
						if (topicos[i].associado){
							if (indiceFinal == -1)
								indiceFinal = i;
						} else if (topicos[i].modificado){
							if (indiceFinal >= 0)
								topicos[i].dataFim = topicos[indiceFinal].dataFim;
							indiceFinal = -1;

							stringTA = topicos[i].id + "%@%" + topicos[i].titulo + "%@%" + topicos[i].dataInicio + "%@%" + topicos[i].dataFim + (stringTA == "" ? "" : "%!%") + stringTA; 
						}
					}
	
					document.getElementById ("form:dadosTeL").value = stringTA;
				}