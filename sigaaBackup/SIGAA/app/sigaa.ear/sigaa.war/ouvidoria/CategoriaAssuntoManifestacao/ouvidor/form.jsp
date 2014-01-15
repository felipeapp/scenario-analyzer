<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<a4j:keepAlive beanName="categoriaAssuntoManifestacao" />

<f:view>
	<h2><ufrn:subSistema /> > ${categoriaAssuntoManifestacao.confirmButton} Categoria do Assunto </h2>

	<table class=formulario width="70%">
		<h:form id="form">
			<caption class="listagem">Informações Sobre a Categoria</caption>
				<tbody>
					<th class="required">Categoria do Assunto:</th>
					<td>
						<h:inputText value= "#{categoriaAssuntoManifestacao.obj.descricao}" size="50" maxlength="50" />	
					</td>			
				</tbody>
			
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton value="#{categoriaAssuntoManifestacao.confirmButton}"
						action="#{categoriaAssuntoManifestacao.cadastrar}" /> 
						<h:commandButton	value="Cancelar" onclick="#{confirm}" action="#{categoriaAssuntoManifestacao.cancelar}" /></td>
				</tr>
			</tfoot>
	</table>
	<br/>
	<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
		class="fontePequena"> Campo de preenchimento obrigatório. </span> <br>
	<br>
	</center>		
		<br /><br />
		<table class="subFormulario" width="70%">
			<caption>Categorias já cadastradas</caption>
			<thead>
					<tr>
						<th >Categoria</th>
						<th>Situação</th>
						
						<th width="2%"></th>
						<th width="2%"></th>
						<th width="2%"></th>
						
					</tr>
			</thead>
			<tbody>
				<c:forEach items="#{categoriaAssuntoManifestacao.categorias }" var="categoria">
					<tr>
						<td style="text-align: left;"> 
							${categoria.descricao }					
						</td>
						
							<c:if test="${categoria.ativo == true}">
									<td style="text-align: left;">ativo</td>
							</c:if>
							<c:if test="${categoria.ativo == false}">
									<td style="text-align: left;">inativo</td>
							</c:if>
						</tr>
				</c:forEach>
			</tbody>
		</table>
		
		</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>