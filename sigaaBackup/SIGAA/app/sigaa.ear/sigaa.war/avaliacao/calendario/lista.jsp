<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h:form>
 <h2><ufrn:subSistema /> > Lista de Avaliações Institucionais</h2>
	<div class="descricaoOperacao">
		Caro usuário,<br/>
		Abaixo estão listadas as Avaliações Institucionais disponíveis para preenchimento. Selecione a que deseja preencher.<br/>
	</div>
			
	<div class="infoAltRem">
		<c:if test="${!calendarioAvaliacaoInstitucionalBean.portalAvaliacaoInstitucional}">
			<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Preencher Avaliação Institucional
		</c:if>
		<c:if test="${calendarioAvaliacaoInstitucionalBean.portalAvaliacaoInstitucional}">
			<ufrn:checkRole papel="<%=SigaaPapeis.COMISSAO_AVALIACAO%>">
				<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;"/>
				<h:commandLink action="#{calendarioAvaliacaoInstitucionalBean.preCadastrar}" value="Cadastrar"/>
				<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: Atualizar
				<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: Remover
			</ufrn:checkRole>
			<h:graphicImage value="/img/view.gif" style="overflow: visible;" />: Visualizar Formulário
		</c:if>
	</div>
	<table class="formulario" width="100%">
		<caption>Lista de Avaliações Institucionais</caption>
		<thead>
			<tr>
				<td style="text-align: center">Ano-Período</td>
				<td>Formulário</td>
				<td>Perfil Entrevistado</td>
				<td style="text-align: center">Ensino à Distância</td>
				<td style="text-align: center">Período de Resposta</td>
				<td width="2%"></td>
				<c:if test="${calendarioAvaliacaoInstitucionalBean.portalAvaliacaoInstitucional}">
					<ufrn:checkRole papel="<%=SigaaPapeis.COMISSAO_AVALIACAO%>">
						<td width="2%"></td>
						<td width="2%"></td>
					</ufrn:checkRole>
					<td width="2%"></td>
				</c:if>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="#{calendarioAvaliacaoInstitucionalBean.allAtivos}" var="item" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td style="text-align: center">${item.ano}.${item.periodo}</td>
					<td>${item.formulario.titulo}</td>
					<td>${item.formulario.descricaoTipoAvaliacao}</td>
					<td style="text-align: center"><ufrn:format valor="${item.formulario.ead}" type="simNao" /></td>
					<td style="text-align: center"><ufrn:format type="data" valor="${item.inicio}" /> à <ufrn:format type="data" valor="${item.fim}"/></td>
					<td width="2%">
						<h:commandLink action="#{calendarioAvaliacaoInstitucionalBean.responderFormulario}" rendered="#{item.periodoPreenchimento && !calendarioAvaliacaoInstitucionalBean.portalAvaliacaoInstitucional}" id="responder">
							<h:graphicImage value="/img/seta.gif" style="overflow: visible;" title="Preencher Avaliação Institucional"/>
							<f:param name="id" value="#{item.id}" />
						</h:commandLink>
					</td>
					<c:if test="${calendarioAvaliacaoInstitucionalBean.portalAvaliacaoInstitucional}">
						<ufrn:checkRole papel="<%=SigaaPapeis.COMISSAO_AVALIACAO%>">
							<td width="2%">
								<h:commandLink action="#{calendarioAvaliacaoInstitucionalBean.atualizar}" id="atualizar">
									<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" title="Atualizar" />
									<f:param name="id" value="#{item.id}" />
								</h:commandLink>
							</td>
							<td width="2%">
								<h:commandLink action="#{calendarioAvaliacaoInstitucionalBean.preRemover}" id="remover">
									<h:graphicImage value="/img/delete.gif" style="overflow: visible;" title="Remover" />
									<f:param name="id" value="#{item.id}" />
								</h:commandLink>
							</td>
						</ufrn:checkRole>
						<td width="2%">
							<h:commandLink action="#{cadastrarFormularioAvaliacaoInstitucionalMBean.view}" id="Visualizar">
								<h:graphicImage value="/img/view.gif" style="overflow: visible;" title="Alterar Formulário da Avaliação Institucional"/>
								<f:param name="id" value="#{item.formulario.id}" />
							</h:commandLink>
						</td>
					</c:if>
				</tr>
			</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="${calendarioAvaliacaoInstitucionalBean.portalAvaliacaoInstitucional ? 8 : 6}">
					<h:commandButton action="#{calendarioAvaliacaoInstitucionalBean.cancelar}" value="Cancelar" onclick="#{ confirm }" />
				</td>
			</tr>
		</tfoot>
	</table>
	<br/>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>