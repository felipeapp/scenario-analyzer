<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<a4j:keepAlive beanName="solicitacaoReposicaoProva"/>
<style>
	table.listagem tr td.periodo {
		background: #C4D2EB;
		padding: 3px;
		font-weight: bold;
	}
</style>

<f:view>
	<h2> <ufrn:subSistema /> &gt; Reposição de Avaliação &gt; Apreciar Solicitações </h2>
	
	<div class="descricaoOperacao">
		<p>
			<b> Caro Usuário, </b>
		</p> 	
		<p>Selecione a turma que deseja analisar as solicitações de reposição de avaliação.</p>		
		<br/>
		<p><b>Art. 101.</b> Impedido de participar de qualquer avaliação, por motivo de caso fortuito ou força
		maior devidamente comprovado e justificado, o aluno tem direito de realizar avaliação de
		reposição.</p>
		<br/>
	</div>	
	
	<div class="infoAltRem" style="font-variant: small-caps;">
		<h:graphicImage value="/img/avancar.gif"style="overflow: visible;"/>: Selecionar Turma
		<h:graphicImage value="/img/baixo.gif" style="overflow: visible;" />: Exibir Avaliações
		<h:graphicImage value="/img/cima.gif" style="overflow: visible;" />: Esconder Avaliações
	</div>
<h:form>
	<table class="formulario" width="50%">
		<caption>FILTROS</caption>
		<tbody>
			<tr>
				<th width=35%" style="text-align:right;">Ano-Período:</th>
				<td>
					<h:inputText value="#{ solicitacaoReposicaoProva.ano }" onkeyup="return formatarInteiro(this);" size="4" maxlength="4"/>	- <h:inputText value="#{ solicitacaoReposicaoProva.periodo }" size="1" maxlength="1" onkeyup="return formatarInteiro(this);"/>
				</td>
			</tr>
			<tr>
				<th width=35%" style="text-align:right;">Status:</th>
				<td>
					<h:selectOneMenu value="#{ solicitacaoReposicaoProva.statusFiltro }" valueChangeListener="#{solicitacaoReposicaoProva.tratarStatusSubmetido}" style="text-align:left;">
						<f:selectItems value="#{ solicitacaoReposicaoProva.statusFiltroCombo}" />
						<a4j:support event="onchange" />  				
					</h:selectOneMenu>
				</td>
			</tr>
			<c:if test="${ !solicitacaoReposicaoProva.analisarSolicitacoes }">
				<tr>
					<th width="20%"><h:selectBooleanCheckbox id="parecerDocente" value="#{ solicitacaoReposicaoProva.parecerDocente }"/></th>
					<td>
						Possui parecer do docente.
					</td>
				</tr>
				<tr>
					<th width="20%"><h:selectBooleanCheckbox id="dentroPrazo" value="#{ solicitacaoReposicaoProva.dentroPrazo }"/></th>
					<td>
						Dentro do prazo.
						<ufrn:help>
							Dentro do prazo de 3 dias úteis após o discente não poder mais enviar solicitações de reposição.
						</ufrn:help>
					</td>
				</tr>
			</c:if>
		</tbody>
		<tfoot>
		<tr>
			<td colspan="2">
				<h:commandButton value="Filtrar" action="#{solicitacaoReposicaoProva.filtrar}"/>
			</td>
		</tr>	
		</tfoot>	
	</table>
	<br/>
	
	<c:if test="${ not empty solicitacaoReposicaoProva.listaSolicitacoes }">
		<table class="listagem" width="80%">
			<thead>
				<tr>
					<td width="2%">
					<td width="60%">Turma</td>
					<td> </td>
				</tr>
			</thead>
			<c:set var="idTurma" value="0" />
			<c:forEach items="#{solicitacaoReposicaoProva.listaSolicitacoes}" var="t" varStatus="loop">
				<c:if test="${idTurma != t.turma.id}">
					<tr>
						<td class="icon">
						<img src="/sigaa/img/baixo.gif" title="Exibir Avaliações" onclick="exibirAvaliacoes(this,${t.id});">
		           		</td>
						<td>${t.turma.descricaoCodigo}</td>
						<td style="width: 30px; text-align: right;">
							<c:if test="${ solicitacaoReposicaoProva.analisarSolicitacoes }">
								<h:commandLink action="#{solicitacaoReposicaoProva.registrarParecer}" id="btTurma" title="Selecionar Turma">
									<h:graphicImage value="/img/avancar.gif"/>
									<f:param name="idTurma" value="#{ t.turma.id }"/>
								</h:commandLink>	
							</c:if>	
							<c:if test="${ !solicitacaoReposicaoProva.analisarSolicitacoes }">
								<h:commandLink action="#{solicitacaoReposicaoProva.registrarHomologacao}" id="btTurma" title="Selecionar Turma">
									<h:graphicImage value="/img/avancar.gif"/>
									<f:param name="idTurma" value="#{ t.turma.id }"/>
								</h:commandLink>	
							</c:if>					
						</td>				
					</tr>
					<tr id='idAvaliacoes_${t.id}' style="display:none;">
						<td colspan="3">
							<table class="listagem" style="width:99%;margin-left:1%">
								<thead>
									<tr>
										<td width="60%">Avaliação</td>
										<td></td>
									</tr>
								</thead>
								<c:forEach items="#{t.avaliacoes}" var="a" varStatus="loop">
										<tr>
											<td>${a.descricao}</td>
											<td style="width: 30px; text-align: right;">
												<c:if test="${ solicitacaoReposicaoProva.analisarSolicitacoes }">
													<h:commandLink action="#{solicitacaoReposicaoProva.registrarParecer}" id="btTurma2" title="Selecionar Turma">
														<h:graphicImage value="/img/avancar.gif"/>
														<f:param name="idTurma" value="#{ t.turma.id }"/>
														<f:param name="idAvaliacao" value="#{ a.id }"/>
													</h:commandLink>	
												</c:if>	
												<c:if test="${ !solicitacaoReposicaoProva.analisarSolicitacoes }">
													<h:commandLink action="#{solicitacaoReposicaoProva.registrarHomologacao}" id="btTurma2" title="Selecionar Turma">
														<h:graphicImage value="/img/avancar.gif"/>
														<f:param name="idTurma" value="#{ t.turma.id }"/>
														<f:param name="idAvaliacao" value="#{ a.id }"/>
													</h:commandLink>	
												</c:if>					
											</td>				
										</tr>
								</c:forEach>	
							</table>
						</td>
					</tr>							
				</c:if>
				<c:set var="idTurma" value="${t.turma.id}" />		
			</c:forEach>
		</table>
	</c:if>
	<c:if test="${ empty solicitacaoReposicaoProva.listaSolicitacoes }">
		<br/>
		<div style="text-align:center;color:red;font-weight:bold;">Nenhuma Solicitação de Reposição de Avaliação encontrada!</div>
		<br/>
	</c:if>
</h:form>

<script type="text/javascript">

	function exibirAvaliacoes (img,idTentativas) {

		var id = "idAvaliacoes_" + idTentativas
		var elem = document.getElementById(id);

		if (elem.style.display == "none") {
			elem.style.display = "";
			img.src="/sigaa/img/cima.gif"
			img.title ="Esconder Avaliações"	
		}		
		else {
			elem.style.display = "none";
			img.src="/sigaa/img/baixo.gif"
			img.title ="Exibir Avaliações"
		}		
	}
</script>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
