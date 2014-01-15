<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<style>
	.centro{
		text-align: center !important;
	}
</style>
<f:view>
	<h2 class="title">
		<ufrn:subSistema /> > Recomendação de Curso
	</h2>
	
	<div class="descricaoOperacao">
		<p>Caro usuário,</p>
		<p> Esta tela exibe todas as recomendações cadastradas para os cursos dos programas de pós-graduação.</p>
	</div>
	
	<c:if test="${not empty recomendacao.all}">
		<h:form id="formListaRecomendacaoPrograma">
		
			<div class="infoAltRem">
				<f:verbatim><h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/></f:verbatim>
				<h:commandLink action="#{recomendacao.preCadastrar}" value="Cadastrar"/>
				<f:verbatim><h:graphicImage value="/img/alterar.gif"/>: </f:verbatim>Alterar
				<f:verbatim><h:graphicImage value="/img/delete.gif"/>: </f:verbatim>Remover
				
			</div>
			
			<table class="listagem">
				<caption class="listagem">Recomendações dos Programas</caption>
				<thead>
					<tr>
						<th>Curso</th>
						<th width="10%">Nível</th>
						<th width="8%" class="centro">Conceito</th>
						<th width="18%">Portaria</th>
						<th width="1%"></th>
						<th width="1%"></th>
					</tr>
				</thead>
				<tbody>
				<c:forEach items="#{recomendacao.all}" var="item" varStatus="i">
					<tr class="${i.index%2==0?'linhaPar':'linhaImpar'}">
						<td ><h:outputText value="#{item.curso.descricaoCompleta}"/></td>
						<td><h:outputText value="#{item.curso.descricaoNivel}"/></td>
						<td  class="centro"><h:outputText value="#{item.conceito}"/></td>
						<td>
							<h:outputText value="#{item.portaria}"/>
							<h:outputText value="NÃO INFORMADA" rendered="#{empty item.portaria}"/>
						</td>
						<td>
							<h:commandLink action="#{recomendacao.atualizar}" id="alterarItemListado">
								<h:graphicImage value="/img/alterar.gif"/>
								<f:param name="id" value="#{item.id}"/>
							</h:commandLink>
						</td>
						<td>
							<h:commandLink action="#{recomendacao.remover}" onclick="#{confirmDelete}" id="removerItemListado">
								<h:graphicImage value="/img/delete.gif"/>
								<f:param name="id" value="#{item.id}"/>
							</h:commandLink>
						</td>
					</tr>
				</c:forEach>
				</tbody>
				<tfoot>
					<tr>
						<td align="center" colspan="6">
							<h:commandButton value="Cancelar" id="btnCancelar" immediate="true"	
							action="#{recomendacao.subSistema}" onclick="#{confirm}"/>
						</td>
					</tr>
				</tfoot>
			</table>
		
		</h:form>
	</c:if>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>