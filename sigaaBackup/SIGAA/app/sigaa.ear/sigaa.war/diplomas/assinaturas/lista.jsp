<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Parâmetros do Registro de Diplomas</h2>

	<h:form id="form">
		
		<div class="infoAltRem">
			<h:graphicImage value="/img/check.png"style="overflow: visible;"/>: Atualmente Ativo
			<h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar/Definir Ativo
			<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>: <h:commandLink action="#{responsavelAssinaturaDiplomasBean.preCadastrar}" value="Cadastrar"/>
		</div>
		
		<c:if test="${empty responsavelAssinaturaDiplomasBean.all}" >
			<br/>
			<div style="text-align: center; color: red;">Não há registros cadastrados.</div> 
		</c:if>
		
		<c:if test="${not empty responsavelAssinaturaDiplomasBean.all}" >
		<table class="listagem">
			<caption>Responsáveis por Assinar Diplomas</caption>
			<thead>
				<tr>
					<th width="3%"></th>
					<th width="10%" style="text-align: center;">Data de Cadastro</th>
					<th width="10%" style="text-align: center;">Nivel de Ensino</th>
					<th width="84">Função/Nome</th>
					<th width="3%"></th>
				</tr>
			</thead>
			<c:forEach var="item" items="#{ responsavelAssinaturaDiplomasBean.all }" varStatus="loop">
				<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					<td valign="top">
						<h:graphicImage value="/img/check.png"style="overflow: visible;" title="Atualmente Ativo" rendered="#{item.ativo}"/>
					</td>
					<td style="text-align: center;" valign="top"><ufrn:format type="data" valor="${ item.dataCadastro }" /></td>
					<td style="text-align: left;" valign="top">${ item.nivelDescricao} </td>
					<td valign="top">
						<b>${item.descricaoFuncaoReitor}:</b> ${item.nomeReitor}<br/>
						<c:if test="${not empty item.nomeDiretorUnidadeDiplomas}">
							<b>${item.descricaoFuncaoDiretorUnidadeDiplomas}:</b> ${item.nomeDiretorUnidadeDiplomas}<br/>
						</c:if>
						<c:if test="${not empty item.nomeDiretorGraduacao}">
							<b>${item.descricaoFuncaoDiretorGraduacao}:</b> ${item.nomeDiretorGraduacao}<br/>
						</c:if>
						<c:if test="${not empty item.descricaoFuncaoDiretorPosGraduacao}"> 
							<b>${item.descricaoFuncaoDiretorPosGraduacao}:</b> ${item.nomeDiretorPosGraduacao}<br/>
						</c:if>
						<c:if test="${not empty item.descricaoFuncaoResponsavelCertificadosLatoSensu}"> 
							<b>${item.descricaoFuncaoResponsavelCertificadosLatoSensu}:</b> ${item.nomeResponsavelCertificadosLatoSensu}<br/>
						</c:if>
					</td>
					<td valign="top">
						<h:commandLink action="#{ responsavelAssinaturaDiplomasBean.atualizar }">
							<f:param name="id" value="#{ item.id }" />
							<f:verbatim>
								<h:graphicImage value="/img/alterar.gif"style="overflow: visible;" title="Alterar/Definir Ativo"/>
							</f:verbatim>
						</h:commandLink>
					</td>
				</tr>
			</c:forEach>
		</table>
		</c:if>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>