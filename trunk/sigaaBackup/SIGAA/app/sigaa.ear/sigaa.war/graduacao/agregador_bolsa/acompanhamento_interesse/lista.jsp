<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2><ufrn:subSistema/> &gt; Minhas inscrições</h2>

<div id="ajuda" class="descricaoOperacao">

	<p><center><strong>Registros de Interesse</strong></center></p>

	<br />

	<p>
		Acompanhe o andamento dos projetos que você registrou interesse em participar.
	</p>
</div>

<h:form>

		<div class="infoAltRem">
		    <h:graphicImage value="/img/email_go.png" 	style="overflow: visible;" />: Enviar Mensagem
			<h:graphicImage value="/img/view.gif" style="overflow: visible;" />: Visualizar Detalhes
	    </div>
		
<table class="listagem" width="90%">
	<caption class="listagem"> Lista de Inscrições</caption>
	<thead>
		<tr>
			<th>Descrição</th>
			<th>Tipo da Bolsa</th>
			<th>Status</th>
			<th></th>
			<th></th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="#{interessadoBolsa.inscricoes}" var="ins" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td>${ ins.descricao }</td>
				<td>${ ins.tipoBolsa }</td>
				<td>
				${ ins.statusAtualBolsa }
				${ ins.matricula == usuario.discenteAtivo.matricula ? '(<font color="green">VOCÊ FOI SELECIONADO)</font>' : '' }
				</td>
				<td>
					<h:commandLink title="Enviar mensagem para o responsável" action="#{agregadorBolsas.iniciarEnviarResponsavel}" style="border: 0;" rendered="#{not empty ins.usuario}" id="envMsg">
						<f:param name="idUsuario" value="#{ins.usuario.id}"/>
						<h:graphicImage url="/img/email_go.png" alt="Enviar mensagem para o responsável"/>
					</h:commandLink>
				</td>
				<td>
					<c:if test="${ins.pesquisa}">
						<a href="/sigaa/pesquisa/planoTrabalho/wizard.do?dispatch=view&obj.id=${ins.idDetalhe}" id="planotrab">
							<h:graphicImage url="/img/view.gif" alt="Visualizar Detalhes"/>
						</a>
					</c:if>
					<c:if test="${ins.extensao}">
						<h:commandLink title="Visualizar Ação" action="#{ atividadeExtensao.view }" id="visualizar_acao_">
							<f:param name="id" value="#{ins.idDetalhe}" />
			                <h:graphicImage url="/img/view.gif" alt="Visualizar Detalhes"/>
						</h:commandLink>
					</c:if>
					<c:if test="${ins.monitoria}">
						<h:commandLink title="Visualizar Projeto" action="#{ projetoMonitoria.view }" id="visualizar_projeto">
							<f:param name="id" value="#{ins.idDetalhe}"/>
			                <h:graphicImage url="/img/view.gif" alt="Visualizar Detalhes"/>
						</h:commandLink>
					</c:if>
					<c:if test="${ins.associada}">
						<h:commandLink title="Visualizar Ação Associada" action="#{ projetoBase.view }" id="visualizar_acao_associada">
							<f:param name="id" value="#{ins.idDetalhe}"/>
			                <h:graphicImage url="/img/view.gif" alt="Visualizar Detalhes"/>
						</h:commandLink>
					</c:if>
					<h:commandLink action="#{agregadorBolsas.viewApoioTecnico}" rendered="#{ins.apoioTecnico}" id="verApoioTecnico">
						<f:param name="id" value="#{ins.idDetalhe}"/>
						<h:graphicImage url="/img/view.gif" alt="Visualizar Detalhes"/>
					</h:commandLink>	
				</td>
			</tr>
		</c:forEach>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="5" align="center">
				<h:commandButton value="<< Voltar" action="#{agregadorBolsas.iniciarBuscar}" id="btaoVoltarr"/>
			</td>
		</tr>
	</tfoot>
</table>


</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>