<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	
	<a4j:keepAlive beanName="bibliotecaMBean"></a4j:keepAlive>
	
	<h2><ufrn:subSistema /> &gt; Bibliotecas </h2>

	<h:form>
	
		<h:messages showDetail="true" />

		<div class="infoAltRem" style="width:80%;">
			<h:graphicImage value="/img/view.gif" style="overflow: visible;" />: 
			Visualizar Configurações da Biblioteca
		</div> 

		<table class="formulario" width="80%">
			<caption class="listagem">Lista de Bibliotecas Ativas do Sistema (${bibliotecaMBean.quantidadeBibliotecasInternasAtivas})</caption>
			<thead>
				<tr>
					<th>Identificador</th>
					<th>Descrição</th>
					<th width="20"></th>
				</tr>
			</thead>
			
			<c:forEach items="#{bibliotecaMBean.allBibliotecasInternasAtivas}" var="bibliotecaInternaAtiva" varStatus="status">
				<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					<td>${bibliotecaInternaAtiva.identificador}</td>
					<td>${bibliotecaInternaAtiva.descricao}</td>
					
					<td>
						<h:commandLink action="#{bibliotecaMBean.preAtualizar}" style="border: 0;">
							<f:param name="id" value="#{bibliotecaInternaAtiva.id}" />
							<h:graphicImage url="/img/view.gif" alt="Alterar Biblioteca" />
						</h:commandLink>
					</td>

				</tr>
			</c:forEach>
			
			<tfoot>
				<tr>
					<td colspan="3" style="text-align: center;">
						<h:commandButton value="<< Voltar" action="#{bibliotecaMBean.cancelar}"></h:commandButton>
					</td>
				</tr>
			</tfoot>
			
		</table>

	</h:form>

</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>