<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > Biblioteca/Unidade Externa</h2>
	
	<h:form>


		<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO
				, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL }  %>">
			<div class="infoAltRem" style="width:80%;">
	
			
				<h:graphicImage value="/img/adicionar.gif" />
				<h:commandLink action="#{bibliotecaExternaMBean.preCadastrar}" value="Nova Biblioteca/Unidade Externa" />
			
	
				<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: 
				Alterar
				<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: 
				Remover
			</div> 
		</ufrn:checkRole>
		

		<table class="listagem" style="width:80%;">
			<caption>Lista de Bibliotecas / Unidades Externas (${bibliotecaExternaMBean.size})</caption>
			<thead>
				<tr>
					<th width="120">Identificador</th>
					<th>Descrição</th>
					<th width="20"></th>
					<th width="20"></th>
				</tr>
			</thead>
			<c:forEach items="#{bibliotecaExternaMBean.all}" var="bibliotecaExterna" varStatus="status">
				<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td>${bibliotecaExterna.identificador}</td>
					<td>${bibliotecaExterna.descricao}</td>
					<td>
						<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL }  %>">
							<h:commandLink action="#{bibliotecaExternaMBean.preAtualizar}">
								<f:param name="id" value="#{bibliotecaExterna.id}" />
								<h:graphicImage url="/img/alterar.gif" alt="Alterar Biblioteca/Unidade Externa" style="border: 0;"/>
							</h:commandLink>
						</ufrn:checkRole>
					</td>
					<td>
						<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL }  %>">
							<h:commandLink title="Remover" style="border: 0;"
								action="#{bibliotecaExternaMBean.remover}" onclick="return confirm('Tem certeza que deseja remover esta Biblioteca Externa?');">
							<f:param name="id" value="#{bibliotecaExterna.id}" />
							<h:graphicImage url="/img/garbage.png" alt="Remover Biblioteca/Unidade Externa" />
							</h:commandLink>
						</ufrn:checkRole>
					</td>
				</tr>
			</c:forEach>
			
			<tfoot>
				<tr>
					<td colspan="4" style="text-align: center;">
						<h:commandButton value="Cancelar" action="#{bibliotecaExternaMBean.cancelar}" />
					</td>
				</tr>
			</tfoot>
			
		</table>

	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>