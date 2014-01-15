<% request.setAttribute("res1024","true"); %>
<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>
<%@taglib uri="/tags/jawr" prefix="jwr"%>

<style type="text/css">
div.opcoes { margin: 5px 0; }
div.opcoes a { font-size: 0.9em; }
tr.alunoSelecionado { background: #FF8888; }
</style>

<script type="text/javascript" src="/shared/jsBundles/jawr_loader.js" ></script>
<script>
	JAWR.loader.script('/javascript/jquery/jquery.js');
</script>
<script>
	var JQuery = jQuery.noConflict();
</script>

<f:view>
	<c:set var="defaultCancel" value="${ ctx }/ensino/consolidacao/detalhesTurma.jsf?avaliacao=true" scope="session"/>
	<c:set var="obj" value="${ turmaVirtual.config }"/>
	<h:outputText value="#{ consolidarTurma.create }"/>
	<h:outputText value="#{ relatorioConsolidacao.create }"/>
	<c:set var="totalNotas" value="0"/>
	<c:set var="somaNotas" value="0"/>	

	<c:set var="bean" value="${ consolidarTurma }"/>
	<c:if test="${ consolidarTurma.turma.id == 0 }">
	<c:set var="bean" value="${ relatorioConsolidacao }"/>
	</c:if>

	<input type="hidden" id="tipoUnid1" value="${ obj.tipoMediaAvaliacoes1 }"/>
	<input type="hidden" id="tipoUnid2" value="${ obj.tipoMediaAvaliacoes2 }"/>
	<input type="hidden" id="tipoUnid3" value="${ obj.tipoMediaAvaliacoes3 }"/>

	<h3>${ bean.turma.descricaoSemDocente }</h3>
	<hr/>
		<br/>
		<div class="notas" style="clear: both;">
			<table class="tabelaRelatorio" width="100%">
			<caption>Alunos Matriculados</caption>
			<thead>
				<tr>
					<th width="10%" style="text-align: right">Matrícula</th>
					<th>Nome</th>
					<c:forEach var="unid" items="${ bean.notas }">
					
						<c:if test="${ bean.ead }">
							<c:if test="${ bean.permiteTutor }"> 
								<th style="text-align: center" colspan="${ unid.numeroAvaliacoes }">Prof. ${ unid.unidade }</th>
							</c:if>	
							<c:if test="${ !bean.permiteTutor }"> 
								<th style="text-align: center" colspan="${ unid.numeroAvaliacoes }">Unid. ${ unid.unidade }</th>
							</c:if>	
						</c:if>
						
						<c:if test="${ !bean.ead }">
							<c:if test="${ bean.avaliacao }">
								<th style="text-align: center" colspan="${ unid.numeroAvaliacoes }">Unid. ${ unid.unidade }</th>
							</c:if>
							<c:if test="${ !bean.avaliacao }">
								<th style="text-align: right" colspan="${ unid.numeroAvaliacoes }">Unid. ${ unid.unidade }</th>
							</c:if>
						</c:if>
						
						
						<c:if test="${ bean.ead && bean.permiteTutor }">
						<th width="10%" style="text-align: center">Prof. ${ unid.unidade } x ${ bean.pesoProfessor }%</th>
						<th width="5%" style="text-align: center">Tutor ${ unid.unidade }</th>
						<th width="10%" style="text-align: center">Tutor ${ unid.unidade } x ${ bean.pesoTutor }%</th>
						<th width="5%" style="text-align: center">Unid. + Tutor</th>
						</c:if>
					</c:forEach>
					<c:if test="${ bean.nota && !bean.lato && (!bean.ead || (bean.ead && bean.duasNotas)) }">
					<th width="5%" style="text-align: right">Recuperação</th>
					</c:if>
					<th width="5%" style="text-align: right;">Resultado</th>
					<c:if test="${ !bean.ead }">
					<th width="5%" style="text-align: right;">Faltas</th>
					</c:if>
					<th width="5%" style="text-align: left;">Sit.</th>
				</tr>
	
				<c:if test="${ bean.avaliacao }">
				<tr id="trAval" bgcolor="#C4D2EB"><th></th><th></th>
				<c:forEach var="unid" items="${ bean.notas }">
					<c:forEach var="avaliacao" items="${ unid.avaliacoes}">
						<th id="aval_${avaliacao.id}" style="text-align: right;">${ avaliacao.abreviacao }</th>
						<input type="hidden" id="abrevAval_${avaliacao.id}" value="${ avaliacao.abreviacao }"/>
						<input type="hidden" id="denAval_${avaliacao.id}" value="${ avaliacao.denominacao }"/>
						<input type="hidden" id="notaAval_${avaliacao.id}" value="${ avaliacao.notaMaxima }"/>
						<input type="hidden" id="pesoAval_${avaliacao.id}" value="${ avaliacao.peso }"/>
					</c:forEach>
					<th id="unid" style="text-align: right">Nota</th>
				</c:forEach>
				<c:if test="${ bean.ead }">				
					<th></th><th></th><th></th>
				</c:if>
				<c:if test="${ !bean.ead }">				
					<th></th><th></th><th></th><th></th>
				</c:if>
				</tr>
				</c:if>
			</thead>
			<tbody> 
			<c:forEach var="matricula" items="${ bean.turma.matriculasDisciplina }" varStatus="i">
				<c:if test="${ acesso.dae 
					  or acesso.ppg 
					  or acesso.formacaoComplementar
					  or (acesso.docente and (consolidarTurma.portalDocente or consolidarTurma.turmaVirtual) ) 
					  or (obj.mostrarTodasAsNotas and (!obj.ocultarNotas or consolidarTurma.turma.consolidada) ) 
					  or (!obj.mostrarTodasAsNotas and (usuario.discenteAtivo.id == matricula.discente.id)) 
					  or acesso.coordenadorCursoLato 
					  or acesso.secretarioLato 
					  or acesso.lato 
					  or acesso.coordenadorCursoGrad
					  or acesso.tecnico
					  or (acesso.cursoEad and matricula.discente.curso.id == bean.cursoAtualCoordenacao.id and consolidarTurma.portalCoordenadorGraduacao )}">

				<tr class="${ i.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					<td nowrap="nowrap" style="text-align: right">${ matricula.discente.matricula } </td>
					<td nowrap="nowrap" style="border-right: 1px solid #888;">${ matricula.discente.pessoa.nome }</td>
	
				
				<c:forEach var="unid" items="${ matricula.notas }" varStatus="loop">
					<%--  Avaliações --%>
					<c:if test="${ bean.avaliacao }">
						<c:forEach var="aval" items="${ unid.avaliacoes }">
							<td style="text-align: right">
								<c:if test="${usuario.discenteAtivo.id != matricula.discente.id 
									or (usuario.discenteAtivo.id == matricula.discente.id and (!obj.ocultarNotas or matricula.consolidada)) }">
									<fmt:formatNumber pattern="#0.0" value="${ aval.nota }"/>
								</c:if>		
							</td>
						</c:forEach>
					</c:if>
					<td style="border-right: 1px solid #888; text-align: right">
						<c:if test="${usuario.discenteAtivo.id != matricula.discente.id 
								or (usuario.discenteAtivo.id == matricula.discente.id and (!obj.ocultarNotas or matricula.consolidada)) }">
							<fmt:formatNumber pattern="#0.0" value="${ unid.notaPreenchida }"/>
						</c:if>	
					</td>
					
					<c:if test="${ bean.ead && bean.permiteTutor }">
					<c:set var="pesoProfessor" value="${ empty unid.nota ? '' : unid.nota * bean.pesoProfessor / 100.0 }"/>
									
					<td style="border-right: 1px solid #888; text-align: center">
					<label>
						<c:if test="${usuario.discenteAtivo.id != matricula.discente.id 
									or (usuario.discenteAtivo.id == matricula.discente.id and (!obj.ocultarNotas or matricula.consolidada)) }">
						<fmt:formatNumber pattern="#0.0" value="${ empty unid.nota ? '' : unid.nota * bean.pesoProfessor / 100.0 }"/>
						</c:if>
					</label>
					</td>
					<c:if test="${ unid.unidade == 1 }">
					<c:set var="pesoTutor1" value="${ empty matricula.notaTutor ? '' : matricula.notaTutor * bean.pesoTutor / 100.0 }"/>		
					<td style="border-right: 1px solid #888; text-align: center">
						<label>
						<c:if test="${ usuario.discenteAtivo.id != matricula.discente.id 
									or (usuario.discenteAtivo.id == matricula.discente.id and (!obj.ocultarNotas or matricula.consolidada))}">
							<fmt:formatNumber pattern="#0.0" value="${ matricula.notaTutor }"/>
						</c:if>
						</label>	
					</td>
					<td style="border-right: 1px solid #888; text-align: center">
						<c:if test="${ usuario.discenteAtivo.id != matricula.discente.id 
									or (usuario.discenteAtivo.id == matricula.discente.id and (!obj.ocultarNotas or matricula.consolidada))}">
							<fmt:formatNumber pattern="#0.0" value="${ empty matricula.notaTutor ? '' : matricula.notaTutor * bean.pesoTutor / 100.0 }"/>
						</c:if>	
					</td>
					<td style="border-right: 1px solid #888; text-align: center">
						<c:if test="${ usuario.discenteAtivo.id != matricula.discente.id 
									or (usuario.discenteAtivo.id == matricula.discente.id and (!obj.ocultarNotas or matricula.consolidada))}">
							<fmt:formatNumber pattern="#0.0" value="${ pesoProfessor == '' || pesoTutor1 == '' ? '' : pesoProfessor + pesoTutor1}"/>
						</c:if>
					</td>	
					</c:if>
					<c:if test="${ unid.unidade == 2 }">
					<c:set var="pesoTutor2" value="${ empty matricula.notaTutor2 ? '' : matricula.notaTutor2 * bean.pesoTutor / 100.0 }"/>		
					<td  style="border-right: 1px solid #888; text-align: center">
						<label>
						<c:if test="${ usuario.discenteAtivo.id != matricula.discente.id 
									or (usuario.discenteAtivo.id == matricula.discente.id and (!obj.ocultarNotas or matricula.consolidada))}">
							<fmt:formatNumber pattern="#0.0" value="${ matricula.notaTutor2 }"/>
						</c:if>
						</label>	
					</td>
					<td style="border-right: 1px solid #888; text-align: center">
						<c:if test="${ usuario.discenteAtivo.id != matricula.discente.id 
									or (usuario.discenteAtivo.id == matricula.discente.id and (!obj.ocultarNotas or matricula.consolidada))}">
							<fmt:formatNumber pattern="#0.0" value="${ empty matricula.notaTutor2 ? '' : matricula.notaTutor2 * bean.pesoTutor / 100.0 }"/>
						</c:if>
					</td>
					<td style="border-right: 1px solid #888; text-align: center">
						<c:if test="${ usuario.discenteAtivo.id != matricula.discente.id 
									or (usuario.discenteAtivo.id == matricula.discente.id and (!obj.ocultarNotas or matricula.consolidada))}">
								<fmt:formatNumber pattern="#0.0" value="${ pesoProfessor == '' || pesoTutor2 == '' ? '' : pesoProfessor + pesoTutor2}"/>
						</c:if>
					</td>	
					</c:if>
					</c:if>
					
				</c:forEach>
			
				<c:if test="${ bean.nota && !bean.lato && (!bean.ead || (bean.ead && bean.duasNotas)) }">
				<td style="border-right: 1px solid #888; text-align: right">
					<c:if test="${ !matricula.consolidada and ( usuario.discenteAtivo.id != matricula.discente.id
								or (usuario.discenteAtivo.id == matricula.discente.id and !obj.ocultarNotas))}"	>
						<fmt:formatNumber pattern="#0.0" value="${ empty matricula.recuperacaoPreenchida ? '' : matricula.recuperacaoPreenchida }"/>
					</c:if>
					<c:if test="${ matricula.consolidada }">
						<fmt:formatNumber pattern="#0.0" value="${ empty matricula.recuperacao ? '' : matricula.recuperacao }"/>
					</c:if>
				</td>
				</c:if>
				<c:if test="${ not bean.competencia and not bean.conceito}">
				<td style="border-right: 1px solid #888; text-align:right">
					<c:if test="${ bean.nota }">
						<c:if test="${ !matricula.consolidada and (usuario.discenteAtivo.id != matricula.discente.id
								or (usuario.discenteAtivo.id == matricula.discente.id and !obj.ocultarNotas))}">
							<fmt:formatNumber pattern="#0.0" value="${ matricula.mediaPreenchida }"/>
						</c:if>
						<c:if test="${ matricula.consolidada }">
						<fmt:formatNumber pattern="#0.0" value="${ matricula.mediaFinal }"/>
						</c:if>
					</c:if>
				</td>
				</c:if>
				
				<c:if test="${ bean.competencia or bean.conceito}">
				<td style="border-right: 1px solid #888; text-align:center">
					<c:if test="${ bean.competencia }">
						<c:if test="${ (usuario.discenteAtivo.id != matricula.discente.id
								or (usuario.discenteAtivo.id == matricula.discente.id and (!obj.ocultarNotas or matricula.consolidada)))}">
						${ empty matricula.apto ? '' : (matricula.apto ? 'Apto' : 'Não Apto') }
						</c:if>		
					</c:if>	
					<c:if test="${ bean.conceito }">
						<c:if test="${ (usuario.discenteAtivo.id != matricula.discente.id
							or (usuario.discenteAtivo.id == matricula.discente.id and (!obj.ocultarNotas or matricula.consolidada)))}">
						${ matricula.conceitoChar }
						</c:if>	
					</c:if>
				</td>		
				</c:if>
				
				<c:if test="${ !bean.ead }">
				<td style="border-right: 1px solid #888; text-align:right;">
				${ matricula.numeroFaltas }
				</td>
				</c:if>
					<td style="text-align: left;">
						<c:if test="${ !matricula.consolidada and (usuario.discenteAtivo.id != matricula.discente.id
								or (usuario.discenteAtivo.id == matricula.discente.id and !obj.ocultarNotas))}"	>
							${ matricula.situacaoPreenchida }
						</c:if>	
						<c:if test="${ matricula.consolidada }">
							${ matricula.situacaoAbrev }
						</c:if>
					</td>
				</tr>
				</c:if>
			</c:forEach>
			</tbody>
			</table>
		</div>
		<br/>
		<c:if test="${ bean.nota and obj.mostrarMediaDaTurma }">
			<c:forEach var="matricula" items="${ bean.turma.matriculasDisciplina }" varStatus="i">
				<c:set var="somaNotas" value="${ somaNotas + matricula.media }"/>
				<c:set var="totalNotas" value="${ totalNotas + 1 }"/>
			</c:forEach>	
			<h3 style="text-align: center">Média da Turma: <fmt:formatNumber value="${ somaNotas/ totalNotas }" pattern="#0.0"/></h3>
		</c:if>
		
</f:view>
<jwr:script src="/javascript/jquery-wtooltip.js"/>
<script type="text/javascript">
function tooltipAval() {
	var aval = JQuery("#trAval > th");
	var avaliacoes = [];
	var unid = 1;
	var tipo = "";
	var id = "";
	var abreviacao = "";
	var peso = "";
	var denominacao = "";
	var nota = "";
	var text = "";
	var calc = "";
				
	aval.each(
			function (i) {
				
				if (this.id.indexOf("aval") != -1) {

					tipo = "";					
					if (unid == 1)
						tipo = JQuery('#tipoUnid1').val();
					else if (unid == 2)  
						tipo = JQuery('#tipoUnid2').val();
					else if (unid == 3)
						tipo = JQuery('#tipoUnid3').val();
			        
					id = this.id.substring(5);  
				  
			  	  	abreviacao = JQuery('#abrevAval_'+id).val();
					peso = JQuery('#pesoAval_'+id).val();
				  	var avaliacao = new Object();
				  	avaliacao.abrev = abreviacao;
				  	avaliacao.peso = peso;
				 	avaliacoes.push(avaliacao);	
			      
		         	denominacao = JQuery('#denAval_'+id).val();
					nota = JQuery('#notaAval_'+id).val();
					
					text = "<b>Avaliação:</b> " + denominacao + 
								"<br/>";		
					if ( nota != "" )
						text +=	"<b>Nota Máxima:</b> " + nota +
								"<br/>";	
					if ( tipo == "P"  )			
						text += "<b>Peso:</b> " + peso;	
        			
					JQuery(this).wTooltip({
		    			content: text,
		    			id: "tooltip"+i
	    			});
		        }

				if (this.id.indexOf("unid") != -1) {

					tipo = "";	
					if (unid == 1)
						tipo = JQuery('#tipoUnid1').val();
					else if (unid == 2)  
						tipo = JQuery('#tipoUnid2').val();
					else if (unid == 3)
						tipo = JQuery('#tipoUnid3').val();
								
					if ( tipo == "P" ){
						text = "<b>Método de Avaliação:</b> Média Ponderada<br/>";
						calc = "(";
						var somaPesos = 0;
						for (var i = 0; (aval = avaliacoes[i]) != null && i < avaliacoes.length; i++ ){
							calc += "(" + aval.abrev + " * " + aval.peso + ")";
							somaPesos += parseInt(aval.peso);
								
							if ( i == avaliacoes.length-1 )
								calc += "";  
							else
								calc += " + ";
							
						}
	
						calc += ")/" + somaPesos;
						text += "<b>Cálculo:</b> " + calc;  
						unid++;			 
					}	

					if ( tipo == "A" ){
						text = "<b>Método de Avaliação:</b> Média Aritmética<br/>";
						calc = "(";
						var numeroAvaliacoes = 0;
						for (var i = 0; (aval = avaliacoes[i]) != null && i < avaliacoes.length; i++ ){
							calc += aval.abrev;
							numeroAvaliacoes++;
								
							if ( i == avaliacoes.length-1 )
								calc += "";  
							else
								calc += " + ";
							
						}
						calc += ")/" + numeroAvaliacoes;
						text += "<b>Cálculo:</b> " + calc;  
						unid++;		 
					}	

					if ( tipo == "S" ){
						text = "<b>Método de Avaliação:</b> Soma das Notas<br/>";
						calc = "";
						var numeroAvaliacoes = 0;
						for (var i = 0; (aval = avaliacoes[i]) != null && i < avaliacoes.length; i++ ){
							calc += aval.abrev;
							numeroAvaliacoes++;
								
							if ( i == avaliacoes.length-1 )
								calc += "";  
							else
								calc += " + ";
							
						}

						text += "<b>Cálculo:</b> " + calc;  
						unid++; 
					}	
					
					if ( avaliacoes != null && avaliacoes.length > 0 )
						JQuery(this).wTooltip({
				    		content: text,
				    		id: "tooltip"+i
				    	});	
					avaliacoes = [];	
				}	
			}		
	);

}
tooltipAval();
</script>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>