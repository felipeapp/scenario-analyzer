<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<script>
	JAWR.loader.script('/javascript/jquery/jquery.js');
</script>
<script>
	// Muda o nome do jQuery para J, evitando conflitos com a Prototype.
	var J = jQuery.noConflict();
</script>

<style type="text/css">
div.opcoes { margin: 5px 0; }
div.opcoes a { font-size: 0.9em; }
tr.alunoSelecionado { background: #FEEAC5; }
h4 { text-align: center; color: red; margin: 10px; }
</style>

<script type="text/javascript">
function adicionarAvaliacao(elem, event) {
	
	var element = Event.element(event);

	// Como o IE n�o implementa o m�todo getElementsByClassName � nescess�rio fazer um tratamento.
	var isMicrosoft = false;
	if ( navigator.appName.indexOf("Microsoft") != -1 )
		isMicrosoft = true;	

	if ( isMicrosoft )
	{
		var aux = $(element.parentNode.parentNode);

		aux.getElementsByClassName = function(className)
		{
			var hasClassName = new RegExp("(?:^|\\s)" + className + "(?:$|\\s)");
			var allElements = aux.getElementsByTagName("*");
			var results = [];
	
			var element;
			for (var i = 0; (element = allElements[i]) != null; i++) {
				if (hasClassName.test(element.className))
					results.push(element);
			}
	
			return results;
		}

		$('unidade').value = aux.getElementsByClassName('unidade-tmp')[0].value;
	}	
	else{
		$('unidade').value = $(element.parentNode.parentNode).getElementsByClassName('unidade-tmp')[0].value;
	}
	return true;
}

function removerAvaliacao(elem, event, msg) {
	var element = Event.element(event);

	// Como o IE n�o implementa o m�todo getElementsByClassName � nescess�rio fazer um tratamento.
	var isMicrosoft = false;
	if ( navigator.appName.indexOf("Microsoft") != -1 )
		isMicrosoft = true;	

	if ( isMicrosoft )
	{	
		var aux = $(element.parentNode.parentNode);
		
		aux.getElementsByClassName = function(className)
		{
			var hasClassName = new RegExp("(?:^|\\s)" + className + "(?:$|\\s)");
			var allElements = aux.getElementsByTagName("*");
			var results = [];
	
			var element;
			for (var i = 0; (element = allElements[i]) != null; i++) {
				if (hasClassName.test(element.className))
					results.push(element);
			}
	
			return results;
		}
		
		$('avaliacao').value = aux.getElementsByClassName('aval-tmp')[0].value
	}
	else
		$('avaliacao').value = $(element.parentNode.parentNode).getElementsByClassName('aval-tmp')[0].value;
	return(confirm(msg));
}

</script>

<f:view>
		
	<c:set var="defaultCancel" value="${ ctx }/ensino/consolidacao/detalhesTurma.jsf?avaliacao=true" scope="session"/>
	<c:if test="${ consolidarTurma.competencia }">
		<c:set var="calcularSituacao" value="situacaoApto(this,true)"/>
	</c:if>
	<c:if test="${ consolidarTurma.conceito }">
		<c:set var="calcularSituacao" value="situacaoConceito(this,true)"/>
	</c:if>
	<c:if test="${ consolidarTurma.nota }">
		<c:set var="calcularSituacao" value="calculaMedia(this,true,${ consolidarTurma.musica })"/>
	</c:if>
				
	<c:if test="${ param['avaliacao'] != null }">
		<h:outputText value="#{ consolidarTurma.atualizarTurma }"/>
	</c:if>

	<%@include file="/portais/docente/menu_docente.jsp" %>

	<h2 style="font-size:1.2em;padding:4px 0 2px 2px;border-bottom:1px solid;margin:0 0 5px;" ><ufrn:subSistema/> &gt; Cadastro de Notas</h2>

	<h3>${ consolidarTurma.turma.descricaoSemDocente }</h3>
	<hr/>
	<fmt:setLocale value="pt_BR"/>
	<h:form>
		<input type="hidden" name="idTurma"  value="${ consolidarTurma.turma.id }"/>
		<input type="hidden" name="gestor" value="${ param['gestor'] }"/>
		<input type="hidden" id="maxFaltas" value="${ consolidarTurma.maxFaltas }"/>
		<input type="hidden" id="maxFaltasTotal" value="${ consolidarTurma.maxFaltasTotal }"/>
		
		<%--M�dia m�nima necess�ria para poder fazer a recupera��o --%>
		<input type="hidden" id="mediaMinimaPossibilitaRecuperacao" value="${ consolidarTurma.mediaMinimaPossibilitaRecuperacao }"/>
		<%--M�dia m�nima necess�ria ap�s todas as avalia��oes, incluindo a recupera��o. --%>
		<input type="hidden" id="mediaMinima" value="${ consolidarTurma.mediaMinima }"/>
		<%--M�dia m�nima necess�ria para passar por m�dia, sem necessidade de recupera��o --%>
		<input type="hidden" id="mediaMinimaPassarPorMedia" value="${ consolidarTurma.mediaMinimaPassarPorMedia }"/>		
		<%--M�dia m�nima necess�ria para ser aprovado --%>
		<input type="hidden" id="mediaMinimaAprovacao" value="${ consolidarTurma.mediaMinimaAprovacao }"/>				
		
		
		<%-- Peso dado a m�dia no calculo da m�dia final, usado quando o discente encontra-se em recupera��o --%>
		<input type="hidden" id="pesoMedia" value="${ consolidarTurma.pesoMedia }"/>
		<%-- Peso dado a Recupera��o no calculo da m�dia final --%>
		<input type="hidden" id="pesoRecuperacao" value="${ consolidarTurma.pesoRecuperacao }"/>
		
		
		<input type="hidden" id="tipoMediaAvaliacoes1" value="${ consolidarTurma.config.tipoMediaAvaliacoes1 }"/>
		<input type="hidden" id="tipoMediaAvaliacoes2" value="${ consolidarTurma.config.tipoMediaAvaliacoes2 }"/>
		<input type="hidden" id="tipoMediaAvaliacoes3" value="${ consolidarTurma.config.tipoMediaAvaliacoes3 }"/>
		<input type="hidden" id="metodologiaEad" value="${ consolidarTurma.umaNota }"/>
		<c:if test="${ consolidarTurma.ead }">
			<input type="hidden" id="permiteTutor" value="${ consolidarTurma.metodologiaAvaliacao.permiteTutor }"/>
			<input type="hidden" id="pesoTutor" value="${ consolidarTurma.pesoTutor }"/>
			<input type="hidden" id="pesoProfessor" value="${ consolidarTurma.pesoProfessor }"/>
		</c:if>
		
		<script type="text/javascript" src="/sigaa/javascript/consolidacao/consolidacao_visualizacao.js"></script>
		
		<div class="descricaoOperacao">
			<ul style="margin-top: 0; padding-left: 0">
				<c:if test="${ consolidarTurma.nota }">
					<li>- Digite as notas das unidades utilizando v�rgula para separar a casa decimal.</li>
				</c:if>
				
				<li>- O campo faltas deve ser preenchido com o n�mero de faltas do aluno durante o per�odo letivo.</li>
				
				<c:if test="${ consolidarTurma.nota }">
					<li>- As notas das unidades n�o v�o para o hist�rico do aluno, no entanto, aparecem em seu portal.</li>
				</c:if>
				
				<c:if test="${ consolidarTurma.conceito || consolidarTurma.competencia }">
					<li>- Os resultados n�o v�o para o hist�rico do aluno, no entanto, aparecem em seu portal.</li>
				</c:if>
				
				<li>- Clique em Salvar para gravar as notas inseridas e continu�-las posteriormente.</li>
				<li>- Ao exportar uma planilha o arquivo gerado n�o deve ser salvo em outra vers�o.</li>
				<li>- A estrutura da planilha n�o deve ser modificada.</li>
				
				<c:if test="${ consolidarTurma.subturma  }">
					<li>- N�o � poss�vel consolidar subturmas separadamente.</li>
					<li>- As notas de todas subturmas devem ser lan�adas antes da consolida��o.</li>
				</c:if>
				
				<c:if test="${ consolidarTurma.obrigatoriedadeDiario }">
					<li>- � necess�rio lan�ar os t�picos de aulas ministrados para efetuar a consolida��o.</li>
					<li>- � necess�rio lan�ar a frequ�ncia para efetuar a consolida��o.</li>
				</c:if>
			</ul>
		</div>
		
				<style>
			.opcoes table tr td{
				text-align:center;
				width:70px;
				vertical-align:top;
			}
			
			.adInfo p {
				text-align:justify;
				margin-bottom:10px;
				padding:10px;
				background-color: #EFF3FA;
				width:60%;
				border: 1px solid #CCDDEE;
			}
		</style>
		
		<c:if test="${ consolidarTurma.config == nul || (consolidarTurma.config != null && !consolidarTurma.config.ocultarNotas) }">	
			<div class="adInfo" align="center">
				<p>
					 Ao salvar as notas, elas ser�o divulgadas aos alunos. 
				 	 � poss�vel <b>ocultar as notas</b> salvas dos alunos ao configurar a turma virtual. 
				 	 Para isso, clique <h:commandLink value="aqui" action="#{consolidarTurma.iniciarConfiguracoesAva}"/> e marque "Sim" na op��o "Ocultar as notas dos alunos." 
					ou clique no bot�o "Salvar e Ocultar"
				</p>
			</div>
		</c:if>	
		<c:if test="${ consolidarTurma.config != null && consolidarTurma.config.ocultarNotas }">
			<div class="adInfo" align="center">
				<p>
					 Ao salvar as notas, elas n�o ser�o divulgadas aos alunos. 
				 	 � poss�vel <b>publicar as notas</b> salvas dos alunos ao configurar a turma virtual. 
				 	 Para isso, clique <h:commandLink value="aqui" action="#{consolidarTurma.iniciarConfiguracoesAva}"/> e marque "N�o" na op��o "Ocultar as notas dos alunos",
				 	 ou clique no bot�o "Salvar e Publicar" 
				</p>
			</div>
		</c:if>
		
		<div class="opcoes" align="center">
			<table>
			<tr>
				<td width="25%" align="center" valign="top"><h:commandButton action="#{ consolidarTurma.voltarTurma }" image="/img/consolidacao/nav_left_red.png" alt="Voltar" title="Voltar" immediate="true"/></td>
				<c:if test="${ consolidarTurma.turma.consolidada }">
				<td width="25%" align="center" valign="top">
					<h:commandLink action="#{ relatorioConsolidacao.imprimirComprovante }" title="Imprimir" target="_blank">
					 <h:graphicImage value="/img/consolidacao/printer.png"/>
					</h:commandLink>
				</td>
				</c:if>
				<c:if test="${ !consolidarTurma.turma.consolidada }">
				<td width="25%" align="center" valign="top"><h:commandButton action="#{ consolidarTurma.exportarPlanilha }" image="/img/consolidacao/planilha.jpg" alt="Exportar Planilha" title="Exportar Planilha" onclick="alert('Para que a planilha gerada seja importada com sucesso, o arquivo n�o deve ser salvo em outro formato. A estrutura da planilha n�o deve ser modificada, ou seja, linhas e colunas n�o devem ser adicionadas ou removidas.');"/></td>
				<td width="25%" align="center" valign="top"><h:commandButton action="#{ consolidarTurma.preImportarPlanilha }" image="/img/consolidacao/planilha_upload.jpg" alt="Importar Planilha" title="Importar Planilha"/></td>
				<td width="25%" align="center" valign="top">
					<h:commandLink action="#{ consolidarTurma.imprimir }" title="Imprimir">
					 <h:graphicImage value="/img/consolidacao/printer.png"/>
					</h:commandLink>
				</td>
				<td width="25%" align="center" valign="top"><h:commandButton action="#{ consolidarTurma.salvar }" image="/img/consolidacao/disk_green.png" alt="Salvar" title="Salvar"/></td>
				<c:if test="${not empty consolidarTurma.config && consolidarTurma.config.ocultarNotas }">
					<td><h:commandButton action="#{ consolidarTurma.salvarPublicar }" image="/img/consolidacao/publicar.png" alt="Salvar e Publicar" title="Salvar e Publicar"/></td>
				</c:if>	
				<c:if test="${ empty consolidarTurma.config || (not empty consolidarTurma.config && !consolidarTurma.config.ocultarNotas) }">
					<td><h:commandButton action="#{ consolidarTurma.salvarOcultar }" image="/img/consolidacao/ocultar.png" alt="Salvar e Ocultar" title="Salvar e Ocultar"/></td>
				</c:if>
				<c:if test="${ consolidarTurma.nota && consolidarTurma.permiteRecuperacao }">
					<td width="25%" align="center" valign="top"><h:commandButton action="#{ consolidarTurma.confirmarParcial }" image="/img/consolidacao/disk_yellow.png" alt="Consolida��o Parcial" title="Consolida��o Parcial" onclick="if(!confirm('A consolida��o parcial tem como objetivo consolidar as matr�culas dos alunos aprovados por m�dia. Ap�s a opera��o, se houverem alunos em recupera��o, a turma continuar� aberta, permitindo a sua consolida��o ap�s a realiza��o da prova de recupera��o. Deseja continuar?')) return false;"/></td>
				</c:if>
				<td width="25%" align="center" valign="top"><h:commandButton action="#{ consolidarTurma.confirmar }" image="/img/consolidacao/disk_blue_ok.png" alt="Finalizar (Consolidar)" title="Finalizar (Consolidar)" onclick="return(confirm('Deseja consolidar a turma? N�o ser� poss�vel desfazer esta opera��o!'))"/></td>
				</c:if>
			</tr>
			<tr>
				<td width="25%" align="center" valign="top"><h:commandLink action="#{ consolidarTurma.voltarTurma }" value="Voltar" immediate="true"/></td>
				<c:if test="${ consolidarTurma.turma.consolidada }">
				<td width="25%" align="center" valign="top"><h:commandLink action="#{ relatorioConsolidacao.imprimirComprovante }" value="Imprimir" target="_blank"/></td>
				</c:if>
				<c:if test="${ !consolidarTurma.turma.consolidada }">
				<td width="25%" align="center" valign="top"><h:commandLink action="#{ consolidarTurma.exportarPlanilha }" value="Exportar Planilha" onclick="alert('Para que a planilha gerada seja importada com sucesso, o arquivo n�o deve ser salvo em outro formato. A estrutura da planilha n�o deve ser modificada, ou seja, linhas e colunas n�o devem ser adicionadas ou removidas.');"/></td> 
				<td width="25%" align="center" valign="top"><h:commandLink action="#{ consolidarTurma.preImportarPlanilha }" value="Importar Planilha"/></td>
				<td width="25%" align="center" valign="top"><h:commandLink action="#{ consolidarTurma.imprimir }" value="Imprimir"/></td>
				<td width="25%" align="center" valign="top"><h:commandLink action="#{ consolidarTurma.salvar }" value="Salvar"/></td>
				<c:if test="${not empty consolidarTurma.config && consolidarTurma.config.ocultarNotas }">
					<td><h:commandLink action="#{ consolidarTurma.salvarPublicar }" value="Salvar e Publicar"/></td>
				</c:if>	
				<c:if test="${ empty consolidarTurma.config || (not empty consolidarTurma.config && !consolidarTurma.config.ocultarNotas) }">
					<td><h:commandLink action="#{ consolidarTurma.salvarOcultar }" value="Salvar e Ocultar"/></td>
				</c:if>
				<c:if test="${ consolidarTurma.nota && consolidarTurma.permiteRecuperacao }">
					<td width="25%" align="center" valign="top"><h:commandLink action="#{ consolidarTurma.confirmarParcial }" value="Consolida��o Parcial" onclick="if(!confirm('A consolida��o parcial tem como objetivo consolidar as matr�culas dos alunos aprovados por m�dia. Ap�s a opera��o, se houver alunos em recupera��o, a turma continuar� aberta, permitindo a sua consolida��o ap�s a realiza��o da prova de recupera��o. Deseja continuar?')) return false;"/></td>
				</c:if>
				<td width="25%" align="center" valign="top"><h:commandLink action="#{ consolidarTurma.confirmar }" value="Finalizar (Consolidar)" onclick="if(!confirm('Deseja consolidar a turma? N�o ser� poss�vel desfazer esta opera��o!')) return false;"/></td>
				</c:if>
			</tr>
			</table>
		</div>

		<div class="notas" style="clear: both;">
			<table class="listagem" width="100%" id="notas-turma">
			<caption>Alunos matriculados</caption>
			<thead>
				<tr>
					<th style="text-align: center">#</th>
					<th width="10%"><p style="text-align: right;padding-right:5px;">Matr�cula</p></th>
					<th>Nome <input type="hidden" name="unidade" id="unidade"/></th>
					<c:forEach var="notaUnidadeIterator" items="${ consolidarTurma.notas }">
						<th style="text-align: center" colspan="${ notaUnidadeIterator.numeroAvaliacoes }">Unid. ${ notaUnidadeIterator.unidade } 
						<c:if test="${ !consolidarTurma.turma.consolidada }">
							<input type="hidden" class="unidade-tmp" value="${ notaUnidadeIterator.unidade }"/>
							<h:commandLink action="#{ cadastrarAvaliacao.telaCadastro }" onclick="return(adicionarAvaliacao(this, event));">
								<h:graphicImage value="/img/consolidacao/add.png" alt="Dividir unidade em avalia��es" title="Desmembrar unidade em mais de uma avalia��o"/>
							</h:commandLink>
						</c:if> 
						</th>
						<c:if test="${ consolidarTurma.ead && consolidarTurma.metodologiaAvaliacao.permiteTutor && !consolidarTurma.estagioEad && !consolidarTurma.turmaFeriasEad && consolidarTurma.duasNotas}">
							<th style="text-align: center">Unid. ${ notaUnidadeIterator.unidade } x ${ consolidarTurma.pesoProfessor }%</th>
							<th style="text-align: center">Tutor ${ notaUnidadeIterator.unidade }</th>
							<th style="text-align: center">Tutor ${ notaUnidadeIterator.unidade } x ${ consolidarTurma.pesoTutor }%</th>
							<th style="text-align: center">Unid. + Tutor</th>
						</c:if>
					</c:forEach>
					<c:if test="${ consolidarTurma.nota  && consolidarTurma.permiteRecuperacao &&  !consolidarTurma.lato && (!consolidarTurma.ead || consolidarTurma.estagioEad || consolidarTurma.turmaFeriasEad) }">
					<th width="5%" style="text-align: center">Recupera��o</th>
					</c:if>
					<c:if test="${ ( consolidarTurma.ead && consolidarTurma.metodologiaAvaliacao.permiteTutor && !consolidarTurma.estagioEad && !consolidarTurma.turmaFeriasEad) && consolidarTurma.umaNota}">
						<th style="text-align: center">Unid. ${ notaUnidadeIterator.unidade } x ${ consolidarTurma.pesoProfessor }%</th>
						<th style="text-align: center">Tutor ${ notaUnidadeIterator.unidade }</th>
						<th style="text-align: center">Tutor ${ notaUnidadeIterator.unidade } x ${ consolidarTurma.pesoTutor }%</th>
						<th style="text-align: center">Unid. + Tutor</th>
					</c:if>
					<c:if test="${ consolidarTurma.nota  && (consolidarTurma.ead && !consolidarTurma.lato && !consolidarTurma.estagioEad && !consolidarTurma.turmaFeriasEad)  }">
					<th width="5%" style="text-align: center">Reposi��o</th>
					</c:if>
					<th width="5%" style="text-align: center">Resultado</th>
					<c:if test="${ !consolidarTurma.ead }">
					<c:if test="${ !consolidarTurma.turma.consolidada }">
					<th width="5%" style="text-align: center">Faltas Calc.</th>
					<th style="text-align: center;"><img src="${ctx}/img/consolidacao/arrow_right.png" style="cursor: pointer;" onclick="transferirTodos()"/></th>
					</c:if>
					<th width="5%" style="text-align: center">Faltas</th>
					</c:if>
					<th width="5%" style="text-align: center">Sit.</th>
				</tr>
	
				<c:if test="${ consolidarTurma.avaliacao }">
					<tr bgcolor="#C4D2EB"><th></th><th></th><th><input type="hidden" name="avaliacao" id="avaliacao"/></th>
					<c:forEach var="notaUnidadeIterator" items="${ consolidarTurma.notas }">
						<c:forEach var="avaliacao" items="${ notaUnidadeIterator.avaliacoes }">
							<c:set var="mediaPonderada" value="avaliacoesMediaPonderada${ avaliacao.unidade.unidade }"/>
							<c:set var="mediaAritmetica" value="avaliacoesMediaAritmetica${ avaliacao.unidade.unidade }"/>
							<c:set var="soma" value="avaliacoesSoma${ avaliacao.unidade.unidade }"/>
							<th style="text-align: right" nowrap="nowrap" title="${ avaliacao.denominacao } <c:if test="${ consolidarTurma.config[mediaPonderada] }">(Peso: ${ avaliacao.peso })</c:if><c:if test="${ consolidarTurma.config[soma] }">(Nota M�xima: ${ avaliacao.notaMaxima })</c:if>">${ avaliacao.abreviacao } 
								<c:if test="${ !consolidarTurma.turma.consolidada }">
								<input type="hidden" id="aval-tmp" class="aval-tmp" value="${ avaliacao.id }"/>
								<h:commandLink title="Remover Avalia��o" action="#{ cadastrarAvaliacao.removerAvaliacao }" onclick="return(removerAvaliacao(this, event, 'Deseja realmente remover esta avalia��o?'));" >
									<h:graphicImage value="/img/consolidacao/delete.png"/>
								</h:commandLink>
								</c:if>
							</th>
						</c:forEach>
						<th style="text-align: center">Nota</th>
						<c:if test="${ consolidarTurma.ead && !consolidarTurma.estagioEad && !consolidarTurma.turmaFeriasEad && consolidarTurma.metodologiaAvaliacao.permiteTutor}">
							<th></th><th></th><th></th>
						</c:if>
					</c:forEach>
					<c:if test="${!consolidarTurma.estagioEad && !consolidarTurma.turmaFeriasEad}">
						<th></th><th></th><th></th><th></th><th></th><th></th>
					</c:if>	
					<c:if test="${consolidarTurma.estagioEad || consolidarTurma.turmaFeriasEad}">
						<th></th><th></th><th></th>
					</c:if>	
					</tr>
				</c:if>
			</thead>
			<tbody>
			<c:forEach var="matricula" items="#{ consolidarTurma.turma.matriculasDisciplina }" varStatus="i">
				<tr id="linha_${ i.index }" class="${ i.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					<td align="right" nowrap="nowrap">
						${i.index+1}
					</td>
					<td nowrap="nowrap" style="text-align: right">${ matricula.discente.matricula }
						<label title="${ empty matricula.situacaoAbrev ? '' : matricula.situacaoAluno }">
						<c:if test="${ matricula.situacaoAbrev != '--' }">
						<img src="${ ctx }/img/consolidacao/situacao_${ fn:toLowerCase(matricula.situacaoAbrev) }.png"/>
						</c:if>
						</label>
					</td>
					<td nowrap="nowrap" style="border-right: 1px solid #888;">
						${ matricula.discente.pessoa.nome }

					</td>
	
				<c:forEach var="notaUnidade" items="${ matricula.notas }" varStatus="loop">
	
	
					<%--  Avalia��es --%>
					<c:if test="${ consolidarTurma.avaliacao }">
						<c:forEach var="aval" items="${ notaUnidade.avaliacoes }">
							<c:set var="tipoMedia" value="tipoMediaAvaliacoes${ aval.unidade.unidade }" scope="page"/>
							
							<td align="right">
							<c:if test="${ !matricula.consolidada }">
							<input type="text" name="aval_${ aval.id }" maxlength="4" peso="${ aval.peso }" 
								onkeydown="return(formataValorNota(this, event, 1))" onkeypress="if(event.keyCode == 13) return false;"
								<c:if test="${ empty aval.notaMaxima }">onblur="formataValorNota(this, event, 1);verificaNotaMaiorDez(this)"</c:if>
						        <c:if test="${ not empty aval.notaMaxima }">onblur="formataValorNota(this, event, 1);verificaNotaMaior(this, ${ aval.notaMaxima }, '${ consolidarTurma.config[tipoMedia] }', false)"</c:if>
								onkeyup="somaAvaliacoes('${ notaUnidade.id }', '${ consolidarTurma.config[tipoMedia] }', ${ consolidarTurma.musica }, this, '${ aval.notaMaxima }' )" value="<fmt:formatNumber pattern="#0.0" value="${ aval.nota }"/>" 
								size="3" class="avaliacao${ notaUnidade.id }" />
							</c:if>
							<c:if test="${ matricula.consolidada }">
							<fmt:formatNumber pattern="#0.0" value="${ aval.nota }"/>
							</c:if>
							</td>
						</c:forEach>
					</c:if>
					<td align="center" style="border-right: 1px solid #888;">
					<c:if test="${ !matricula.consolidada }">
						<input type="text" maxlength="4" name="nota_${ notaUnidade.id }" id="nota_${ notaUnidade.id }" class="${ notaUnidade.peso }" value="<fmt:formatNumber pattern="#0.0" value="${ notaUnidade.nota }"/>" size="3"
							onkeypress="if(event.keyCode == 13) return false;"
							<c:if test="${ not empty notaUnidade.avaliacoes }"> 
							 readonly="readonly" style="background: #FDF3E1;"
							</c:if>
							<c:if test="${ empty notaUnidade.avaliacoes }">
							onkeydown="return(formataValorNota(this, event, 1))" onblur="verificaNotaMaiorDez(this);" onkeyup="calculaMedia(this,true,${consolidarTurma.musica});" onfocus="this.select()"
							</c:if>
						/>
					</c:if>
					<c:if test="${ matricula.consolidada }">
					<fmt:formatNumber pattern="#0.0" value="${ notaUnidade.nota }"/>
					</c:if>
					</td>
					
					<c:if test="${ (consolidarTurma.ead && consolidarTurma.metodologiaAvaliacao.permiteTutor && !consolidarTurma.estagioEad && !consolidarTurma.turmaFeriasEad ) && consolidarTurma.duasNotas}">
						<td align="center" style="border-right: 1px solid #888;"><label class="resultadoUnidEad_${ notaUnidade.id }"><fmt:formatNumber pattern="#0.0" value="${ notaUnidade.nota * consolidarTurma.pesoProfessor / 100.0 }"/></label></td>
						<c:if test="${ notaUnidade.unidade == 1 }">
							<td align="center" style="border-right: 1px solid #888;"><label id="tutorNota_${i.index }_1"><fmt:formatNumber pattern="#0.0" value="${ matricula.notaTutor }"/></label></td>
							<td align="center" style="border-right: 1px solid #888;"><label id="tutorPesoUnidade_${i.index }_1"><fmt:formatNumber pattern="#0.0" value="${ matricula.notaTutor * consolidarTurma.pesoTutor / 100.0 }"/></label></td>
							<td align="center" style="border-right: 1px solid #888;"><label id="somaNota_${i.index }_1"><fmt:formatNumber pattern="#0.0" value="${ (notaUnidade.nota * consolidarTurma.pesoProfessor / 100.0) + (matricula.notaTutor * consolidarTurma.pesoTutor / 100.0) }"/></label></td>
						</c:if>
						<c:if test="${ notaUnidade.unidade == 2 }">
							<td align="center" style="border-right: 1px solid #888;"><label id="tutorNota_${i.index }_2"><fmt:formatNumber pattern="#0.0" value="${ matricula.notaTutor2 }"/></label></td>
							<td align="center" style="border-right: 1px solid #888;"><label id="tutorPesoUnidade_${i.index }_2"><fmt:formatNumber pattern="#0.0" value="${ matricula.notaTutor2 * consolidarTurma.pesoTutor / 100.0 }"/></label></td>
							<td align="center" style="border-right: 1px solid #888;"><label id="somaNota_${i.index }_2"><fmt:formatNumber pattern="#0.0" value="${ (notaUnidade.nota * consolidarTurma.pesoProfessor / 100.0) + (matricula.notaTutor2 * consolidarTurma.pesoTutor / 100.0) }"/></label></td>
						</c:if>
					</c:if>
					
					<c:if test="${ (consolidarTurma.ead && consolidarTurma.metodologiaAvaliacao.permiteTutor && !consolidarTurma.estagioEad && !consolidarTurma.turmaFeriasEad ) && consolidarTurma.umaNota }">
						<td align="center" style="border-right: 1px solid #888;"><label class="resultadoUnid_${ notaUnidade.id }"><fmt:formatNumber pattern="#0.0" value="${ notaUnidade.nota * consolidarTurma.pesoProfessor / 100.0 }"/></label></td>
						<td align="center" style="border-right: 1px solid #888;"><label id="tutor_${i.index }_1"><fmt:formatNumber pattern="#0.0" value="${ matricula.notaTutor }"/></label></td>
						<td align="center" style="border-right: 1px solid #888;"><label id="tutorPeso_${i.index }_1"><fmt:formatNumber pattern="#0.0" value="${ matricula.notaTutor * consolidarTurma.pesoTutor / 100.0 }"/></label></td>
						<td align="center" style="border-right: 1px solid #888;"><label id="notaGeral_${i.index }_1"><fmt:formatNumber pattern="#0.0" value="${ (notaUnidade.nota * consolidarTurma.pesoProfessor / 100.0) + (matricula.notaTutor * consolidarTurma.pesoTutor / 100.0) }"/></label></td>
					</c:if>
					
					<c:if test="${ !matricula.consolidada }">
						<script type="text/javascript">
							isExibirNota('${ notaUnidade.id }','${consolidarTurma.duasNotas}')
						</script>
					</c:if>
				</c:forEach>
	
				<c:if test="${ consolidarTurma.nota  && consolidarTurma.permiteRecuperacao &&  !consolidarTurma.lato && (!consolidarTurma.ead || consolidarTurma.estagioEad || consolidarTurma.turmaFeriasEad) }">
				<td align="center" style="border-right: 1px solid #888;">
					<c:if test="${ !matricula.consolidada }">
					<input type="text" maxlength="4" onkeydown="return(formataValorNota(this, event, 1))" onkeypress="if(event.keyCode == 13) return false;" onblur="verificaNotaMaiorDez(this)" onkeyup="calculaMedia(this,true,${ consolidarTurma.musica })" name="recup_${ matricula.id }" value="<fmt:formatNumber pattern="#0.0" value="${ empty matricula.recuperacao ? '' : matricula.recuperacao }"/>" size="3" class="recuperacao"/>					
					</c:if>
					<c:if test="${ matricula.consolidada }">
					<fmt:formatNumber pattern="#0.0" value="${ empty matricula.recuperacao ? '' : matricula.recuperacao }"/>
					</c:if>
				</td>
				</c:if>
	
				<c:if test="${ consolidarTurma.nota  && (consolidarTurma.ead && !consolidarTurma.estagioEad && !consolidarTurma.lato && !consolidarTurma.turmaFeriasEad) }">
				<td align="center" style="border-right: 1px solid #888;">
					<c:if test="${ !matricula.consolidada }">
						<input type="text" maxlength="4" onkeydown="return(formataValorNota(this, event, 1))" onkeypress="if(event.keyCode == 13) return false;" onblur="verificaNotaMaiorDez(this)" onkeyup="calculaMedia(this,true,${ consolidarTurma.musica })" name="recup_${ matricula.id }" value="<fmt:formatNumber pattern="#0.0" value="${ empty matricula.recuperacao ? '' : matricula.recuperacao }"/>" size="3" class="recuperacao"/>
					</c:if>
					<c:if test="${ matricula.consolidada }">
					<fmt:formatNumber pattern="#0.0" value="${ empty matricula.recuperacao ? '' : matricula.recuperacao }"/>
					</c:if>
				</td>
				
				
				</c:if>
	
				<td align="center">
					<c:if test="${ consolidarTurma.nota }">
					<c:if test="${ !matricula.consolidada }">
					<label class="resultado">${ matricula.media == null ? '--' : '' }<fmt:formatNumber pattern="#0.0" value="${ matricula.media }"/></label>
					</c:if>
					<c:if test="${ matricula.consolidada }">
					<label><fmt:formatNumber pattern="#0.0" value="${ matricula.mediaFinal }"/></label>
					</c:if>
					</c:if>
					<c:if test="${ consolidarTurma.conceito }">
						<c:if test="${ !matricula.consolidada }">
						<select name="conceito_${ matricula.id }" class="comboConceito" onchange="situacaoConceito(this,true)">
							<option value="-1">-</option>
							<c:forEach var="conceito" items="${ consolidarTurma.conceitos }">
								<option value="${ conceito.valor }" ${ (matricula.conceito == conceito.valor) ? 'selected="selected"' : '' }>${ conceito.conceito }</option>
							</c:forEach>
						</select>
						</c:if>
						<c:if test="${ matricula.consolidada }">
						${ matricula.conceitoChar }
						</c:if>
					</c:if>
					<c:if test="${ consolidarTurma.competencia }">
						<c:if test="${ !matricula.consolidada }">
						<select name="competencia_${ matricula.id }" onchange="situacaoApto(this,true)">
							<option value="null" ${ (matricula.apto == null) ? 'selected="selected"' : '' }>-</option>
							<option value="true" ${ (matricula.apto != null && matricula.apto) ? 'selected="selected"' : '' }>Apto</option>
							<option value="false" ${ (matricula.apto != null && !matricula.apto) ? 'selected="selected"' : '' }>N&atilde;o Apto</option>
						</select>
						</c:if>
						<c:if test="${ matricula.consolidada }">
						${ matricula.apto ? 'Apto' : 'N�o Apto' }
						</c:if>
					</c:if>
				</td>
				
				<c:if test="${ !consolidarTurma.ead }">
				<c:if test="${ !consolidarTurma.turma.consolidada }">
				<%-- Faltas Calculadas --%>
				<td align="center">
				<c:if test="${ !matricula.consolidada }">
				<input type="text" value="${ matricula.faltasCalculadas }" size="3" readonly="readonly" style="background: #FDF3E1;" class="faltas-calc" onkeypress="if(event.keyCode == 13) return false;"/>
				</c:if>
				</td>
				
				<%-- Transferir Faltas --%>
				<th><img title="Transferir as faltas calculadas da lista de freq��ncia para o campo de faltas do aluno" style="cursor: pointer;" src="${ctx}/img/consolidacao/arrow_right.png" class="img-transferir" onclick="transferir(this); calculaMedia(this,true,${ consolidarTurma.musica }); ${ calcularSituacao }"/></th>
				</c:if>
				<%-- Faltas Digitadas --%>
				<td align="center">
				<c:if test="${ !matricula.consolidada }">
				<input 	type="text" 
						name="faltas_${ matricula.id }" 
						value="${ matricula.numeroFaltas }" 
						size="3" maxlength="3" 
						class="faltas" 
						onblur="verificaFaltasMaiorTotal(this)" 
						onkeydown="return(formataFaltas(this, event))" 
						onkeyup="${ calcularSituacao }" 
						onkeypress="if(event.keyCode == 13) return false;"
						onchange="if( ${ !consolidarTurma.conceito } ) calculaMedia(this,true,${ consolidarTurma.musica });"/>
				</c:if>
				<c:if test="${ matricula.consolidada }">
				${ matricula.numeroFaltas }
				</c:if>
				</td>
				
				</c:if>
				<%--Situa��o --%>
				<td align="center">
				<a href="javascript:void(0)" style="cursor: text; background: none; border: none; color: #000; font-weight: normal" ><label title="${ matricula.situacaoCompleta }" class="situacao">${ matricula.situacaoAbrev }</label></a>
				</td>
				</tr>
				<c:if test="${ !matricula.consolidada }">
					<script type="text/javascript">
						J(document).ready(function () {
							exibirNota(J("#linha_${ i.index }"),${consolidarTurma.conceito},${consolidarTurma.competencia},${consolidarTurma.musica});
						});
					</script>
				</c:if>
				</c:forEach>
			</tbody>
			</table>
		</div>
		
		<div class="opcoes">
		
			<table align="right" width="35%">
			<tr>
				<td width="25%" align="center" valign="top"><h:commandButton action="#{ consolidarTurma.voltarTurma }" image="/img/consolidacao/nav_left_red.png" alt="Voltar" title="Voltar" immediate="true"/></td>
				<c:if test="${ !consolidarTurma.turma.consolidada }">
				<td width="25%" align="center" valign="top"><h:commandButton action="#{ consolidarTurma.exportarPlanilha }" image="/img/consolidacao/planilha.jpg" alt="Exportar Planilha" title="Exportar Planilha" onclick="alert('Para que a planilha gerada seja importada com sucesso, o arquivo n�o deve ser salvo em outro formato. A estrutura da planilha n�o deve ser modificada, ou seja, linhas e colunas n�o devem ser adicionadas ou removidas.');"/></td>
				<td width="25%" align="center" valign="top"><h:commandButton action="#{ consolidarTurma.preImportarPlanilha }" image="/img/consolidacao/planilha_upload.jpg" alt="Importar Planilha" title="Importar Planilha"/></td>
				</c:if>
				<td width="25%" align="center" valign="top">
					<h:commandLink action="#{ consolidarTurma.imprimir }" title="Imprimir">
					 <h:graphicImage value="/img/consolidacao/printer.png"/>
					</h:commandLink>
				</td>
				<c:if test="${ !consolidarTurma.turma.consolidada }">
				<td width="25%" align="center" valign="top"><h:commandButton action="#{ consolidarTurma.salvar }" image="/img/consolidacao/disk_green.png" alt="Salvar" title="Salvar"/></td>
				<c:if test="${ not empty consolidarTurma.config && consolidarTurma.config.ocultarNotas }">
					<td width="25%" align="center" valign="top"><h:commandButton action="#{ consolidarTurma.salvarPublicar }" image="/img/consolidacao/publicar.png" alt="Salvar e Publicar" title="Salvar e Publicar"/></td>
				</c:if>	
				<c:if test="${ empty consolidarTurma.config || (not empty consolidarTurma.config && !consolidarTurma.config.ocultarNotas) }">
					<td width="25%" align="center" valign="top"><h:commandButton action="#{ consolidarTurma.salvarOcultar }" image="/img/consolidacao/ocultar.png" alt="Salvar e Ocultar" title="Salvar e Ocultar"/></td>
				</c:if>
				<td width="25%" align="center" valign="top"><c:if test="${ consolidarTurma.nota && consolidarTurma.permiteRecuperacao }"><h:commandButton action="#{ consolidarTurma.confirmarParcial }" image="/img/consolidacao/disk_yellow.png" alt="Consolida��o Parcial" title="Consolida��o Parcial" onclick="if(!confirm('A consolida��o parcial tem como objetivo consolidar as matr�culas dos alunos aprovados por m�dia. Ap�s a opera��o, se houverem alunos em recupera��o, a turma continuar� aberta, permitindo a sua consolida��o ap�s a realiza��o da prova de recupera��o. Deseja continuar?')) return false;"/></c:if></td>
				<td width="25%" align="center" valign="top"><h:commandButton action="#{ consolidarTurma.confirmar }" image="/img/consolidacao/disk_blue_ok.png" alt="Finalizar (Consolidar)" title="Finalizar (Consolidar)" onclick="return(confirm('Deseja consolidar a turma? N�o ser� poss�vel desfazer esta opera��o!'))"/></td>
				</c:if>
			</tr>

			<tr>
				<td width="25%" align="center" valign="top"><h:commandLink action="#{ consolidarTurma.voltarTurma }" value="Voltar" immediate="true"/></td>
				<c:if test="${ !consolidarTurma.turma.consolidada }">
				<td width="25%" align="center" valign="top"><h:commandLink action="#{ consolidarTurma.exportarPlanilha }" value="Exportar Planilha" onclick="alert('Para que a planilha gerada seja importada com sucesso, o arquivo n�o deve ser salvo em outro formato. A estrutura da planilha n�o deve ser modificada, ou seja, linhas e colunas n�o devem ser adicionadas ou removidas.');"/></td>
				<td width="25%" align="center" valign="top"><h:commandLink action="#{ consolidarTurma.preImportarPlanilha }" value="Importar Planilha"/></td>
				</c:if>
				<td width="25%" align="center" valign="top"><h:commandLink action="#{ consolidarTurma.imprimir }" value="Imprimir" /></td>
				<c:if test="${ !consolidarTurma.turma.consolidada }">
				<td width="25%" align="center" valign="top"><h:commandLink action="#{ consolidarTurma.salvar }" value="Salvar"/></td>
				<c:if test="${ not empty consolidarTurma.config && consolidarTurma.config.ocultarNotas }">
					<td width="25%" align="center" valign="top"><h:commandLink action="#{ consolidarTurma.salvarPublicar }" value="Salvar e Publicar"/></td>
				</c:if>	
				<c:if test="${ empty consolidarTurma.config || (not empty consolidarTurma.config && !consolidarTurma.config.ocultarNotas) }">
					<td width="25%" align="center" valign="top"><h:commandLink action="#{ consolidarTurma.salvarOcultar }" value="Salvar e Ocultar"/></td>
				</c:if>
				<td width="25%" align="center" valign="top"><c:if test="${ consolidarTurma.nota && consolidarTurma.permiteRecuperacao }"><h:commandLink action="#{ consolidarTurma.confirmarParcial }" value="Consolida��o Parcial"  onclick="if(!confirm('A consolida��o parcial tem como objetivo consolidar as matr�culas dos alunos aprovados por m�dia. Ap�s a opera��o, se houverem alunos em recupera��o, a turma continuar� aberta, permitindo a sua consolida��o ap�s a realiza��o da prova de recupera��o. Deseja continuar?')) return false;"/></c:if></td>
				<td width="25%" align="center" valign="top"><h:commandLink action="#{ consolidarTurma.confirmar }" value="Finalizar (Consolidar)" onclick="if(!confirm('Deseja consolidar a turma? N�o ser� poss�vel desfazer esta opera��o!')) return false;" /></td>
				</c:if>
			</tr>
			</table>
		</div>
		
		<div class="legenda">
			<img src="${ctx}/img/consolidacao/arrow_right.png"/><strong> - Transferir as faltas calculadas da lista de freq��ncia para o campo de faltas do aluno.</strong><br/>
			<img src="${ ctx }/img/consolidacao/add.png"/><strong> - Desmembrar unidade em mais de uma avalia��o</strong><br/>
			<img src="${ ctx }/img/consolidacao/delete.png"/><strong> - Remover avalia��o</strong><br/>
			<img src="${ ctx }/img/consolidacao/situacao_apr.png"/><strong> - Aluno Aprovado</strong><br/>
			<img src="${ ctx }/img/consolidacao/situacao_rec.png"/><strong> - Aluno em Recupera��o</strong><br/>
			<img src="${ ctx }/img/consolidacao/situacao_rep.png"/><strong> - Aluno Reprovado</strong>
		</div>
		
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
<script type="text/javascript" src="/sigaa/javascript/consolidacao/consolidacao.js"></script>
<c:if test="${ !consolidarTurma.ead || consolidarTurma.estagioEad || (consolidarTurma.ead && consolidarTurma.lato)  }">
<script type="text/javascript" src="/sigaa/javascript/consolidacao/consolidacao_geral.js"></script>
</c:if>
<c:if test="${ consolidarTurma.ead  && !consolidarTurma.estagioEad && !consolidarTurma.lato }">
	<c:if test="${ consolidarTurma.umaNota }">
	<script type="text/javascript" src="/sigaa/javascript/consolidacao/consolidacao_ead_umanota.js"></script>
	</c:if>
	<c:if test="${ consolidarTurma.duasNotas }">
	<script type="text/javascript" src="/sigaa/javascript/consolidacao/consolidacao_ead_duasnotas.js"></script>
	</c:if>
</c:if>
<c:remove var="msgFlash" scope="session"/>