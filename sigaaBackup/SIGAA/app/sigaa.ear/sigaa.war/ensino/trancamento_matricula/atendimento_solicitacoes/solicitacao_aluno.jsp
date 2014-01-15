<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/sigaa/javascript/graduacao/solicitacao_matricula.js"></script>

<c:set var="confirmarOrientacao" value="if (!confirm('Tem certeza que deseja confirmar estas orientações de trancamento?')) return false" scope="request"/>

<%@page import="br.ufrn.sigaa.ensino.jsf.AtenderTrancamentoMatriculaMBean"%>
<script type="text/javascript">

	var prefixTr = "tr_orientacao_";

	function showReplica(check){
		
		var id = check.id.substring(9);
		
		elementName = prefixTr + id;
		
		var elemento = getEl(elementName);
		if (check.checked) {
			elemento.setDisplayed(true);
		} else {
			elemento.setDisplayed(false);
		}
	}

	var prefixConcordo = "concordo_";
	var prefixOrientar = "orientar_";

	function desmarcarOutro(check){
		var id = check.id.substring(9);
		if( check.name == "aceitos" ){
			var elemento = getEl( prefixOrientar + id ).dom;
			elemento.checked = false;
		} else if( check.name == "orientacoes" ){
			var elemento = getEl( prefixConcordo + id ).dom;
			elemento.checked = false;
		}
	}

</script>

<f:view>

	<%@include file="/graduacao/menu_coordenador.jsp"%>
	<h:outputText value="#{atenderTrancamentoMatricula.create}"></h:outputText>

	<h2> Atendimento de Solicitação de Trancamento de Matrícula </h2>

	<c:if test="${ not atenderTrancamentoMatricula.distancia }">
		<div class="descricaoOperacao">
		<p>
		Caro ${acesso.coordenadorCursoGrad ? 'Coordenador,' : 'Orientador Acadêmico'}
		</p><br/>
		<p>
		Esta operação mostra a lista de disciplinas que o aluno solicitou para trancar.
		Você pode orientar o aluno para que ele desista do trancamento, ou apenas marcar a solicitação como vista.
		</p>
		<p>
		Todas as solicitações de trancamento dos alunos serão aprovadas em 7 dias após a solicitação ou ao fim
		do prazo máximo para o trancamento definido no calendário acadêmico.
		Até então o aluno poderá desistir do trancamento a qualquer momento, bastando para isso cancelar a solicitação.
		</p>
		<p>
		Através desta operação, a coordenação pode orientar o aluno para que ele faça a melhor escolha.
		</p>

	</div>
	</c:if>

	<c:set var="discente" value="#{atenderTrancamentoMatricula.discente}"/>
	<%@include file="/graduacao/info_discente.jsp"%>

	<center>
		<h:form>
			<input type="hidden" value="${atenderTrancamentoMatricula.discente.id}" name="id" />
			<h:graphicImage url="/img/report.png"></h:graphicImage>
			<h:commandLink value="Clique aqui " action="#{historico.selecionaDiscenteForm}">
				<f:param value="#{atenderTrancamentoMatricula.discente.id}" name="id"/>
			</h:commandLink>
			para visualizar histórico do aluno
			
			<br/>
			<a href="javascript:void(0);" onclick="PainelSolicitacoesMatricula.show(${atenderTrancamentoMatricula.discente.id}, true);">
				<img src="/sigaa/img/view.gif" alt="" class="noborder" title="Ver Plano de Matrícula"/> Clique aqui  
			</a>para visualizar as orientações de matrícula
		</h:form>
	</center>
	<br/>

	<h:form>
	<table class="listagem">
		<caption>Solicitações de Trancamento de Matrícula Pendentes do Aluno</caption>

		<thead>
		<tr>
			<th width="40%"> Disciplina </th>
			<th> Turma </th>
			<th style="text-align: center"> Solicitado em</th>
			<%--
			<th> Situação </th>
			<th> </th>
			--%>
			<th> </th>
			<th> </th>
		</tr>
		</thead>
		<tbody>

			<c:forEach var="solicitacao" items="${atenderTrancamentoMatricula.solicitacoes}" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
				<td><b>${solicitacao.matriculaComponente.componenteDescricaoResumida}</b></td>
				<td>${solicitacao.matriculaComponente.turma.codigo}</td>
				<td style="text-align: center">  
					<ufrn:format type="data" valor="${solicitacao.dataCadastro}" />
				</td>
				<%--
				<td>${solicitacao.situacaoString}</td>
				--%>
				<td>
					<input type="checkbox" class="noborder" name="aceitos" value="${solicitacao.id}" id="concordo_${solicitacao.id}" onclick="showReplica(this); desmarcarOutro(this)"><label for="concordo_${solicitacao.id}">Visto</label>
				</td>
				<td>
					<input type="checkbox" class="noborder" name="orientacoes" value="${solicitacao.id}" id="orientar_${solicitacao.id}" onclick="showReplica(this); desmarcarOutro(this)"><label for="orientar_${solicitacao.id}">Orientar Não Trancamento</label>
				</td>
			</tr>

			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">

				<td colspan="5" style="font-variant: small-caps;">
				<em>Motivo do Trancamento: </em>
					<c:if test="${not empty solicitacao.justificativa}">
						${solicitacao.justificativa}
					</c:if>
					<c:if test="${empty solicitacao.justificativa}">
						${solicitacao.motivo.descricao}
					</c:if>
				</td>

			</tr>

			<tr class="tr_orientacao ${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" id="tr_orientacao_${solicitacao.id}">
				<td colspan="5">
					<span style="font-variant: small-caps;"><em>Orientação:</em></span>
					<br/>
					<textarea rows="1" style="width: 90%; margin-left: 30px;" id="orientacao_${solicitacao.id}" name="orientacao_${solicitacao.id}"></textarea>
				</td>
				</tr>
			</c:forEach>

		</tbody>
		<tfoot>
			<tr>
				<td colspan="5" align="center">
					<h:commandButton value="Confirmar" action="#{atenderTrancamentoMatricula.atenderSolicitacao}" onclick="#{confirmarOrientacao}"/>
					<h:commandButton value="Voltar" action="#{atenderTrancamentoMatricula.iniciarAtendimentoSolicitacao}" />
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{atenderTrancamentoMatricula.cancelar}" />
				</td>
			</tr>
		</tfoot>
	</table>
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

<script type="text/javascript">
	var lista = getEl(document).getChildrenByClassName('tr_orientacao')
	for (i = 0; i < lista.size(); i++) {
		lista[i].setDisplayed(false);
	}
</script>