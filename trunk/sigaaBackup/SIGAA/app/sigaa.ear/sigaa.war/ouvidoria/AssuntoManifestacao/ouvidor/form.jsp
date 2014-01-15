<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<a4j:keepAlive beanName="assuntoManifestacao" />

	<f:view>
		<h2>
			<ufrn:subSistema /> &gt; ${assuntoManifestacao.confirmButton} Assunto
		</h2>

		<h:form id="form">
			
			<table class="formulario" width="80%">
				<caption>Informações Sobre o Assunto</caption>
				<tbody>
					
					<tr>
						<th class="required"> Categoria:</th>
						<td>
							<h:selectOneMenu id="categoria" value="#{assuntoManifestacao.obj.categoriaAssuntoManifestacao.id}" onchange="submit()" onclick="changeLista(this)" >
						 		<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
						 		<f:selectItems value="#{categoriaAssuntoManifestacao.allCategoriasAtivasCombo}" />
					 		</h:selectOneMenu>
						</td>
					</tr>
					<tr>
						<th class="required">Assunto:</th>
						<td>
							<h:inputText  value="#{assuntoManifestacao.obj.descricao }" size="50" maxlength="50"/>	
						</td>
					</tr>		
				</tbody>
			
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton id="btn_proximo" value="#{assuntoManifestacao.confirmButton}" action="#{assuntoManifestacao.cadastrar}" />
						<h:commandButton id="btn_cancelar" value="Cancelar"  action="#{assuntoManifestacao.cancelar}" onclick="#{confirm}"/>
					</td>
				</tr>
			</tfoot>	
			</table>
			<br/>
			
		<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
			class="fontePequena"> Campo de preenchimento obrigatório. </span> <br>
			<br>
		</center>
			
		
			<br /><br />
			<table class="subFormulario" width="60%" id="assuntosPorCategoria">
				<c:if test="${not empty assuntoManifestacao.assuntosPorCategoria }">
						<caption>Assuntos já cadastrados para a categoria selecionada</caption>
						
						<thead>
								<tr>
									<th >Assunto</th>
									<th>Situação</th>
									
									<th width="2%"></th>
									<th width="2%"></th>
									
								</tr>
						</thead>
						<tbody>
							<c:forEach items="#{assuntoManifestacao.assuntosPorCategoria }" var="assunto">
								<tr>
									<td style="text-align: left;"> ${assunto.descricao }</td>
															
									<c:if test="${assunto.ativo == true}">
										<td style="text-align: left;">ativo</td>
									</c:if>
									<c:if test="${assunto.ativo == false}">
										<td style="text-align: left;">inativo</td>
									</c:if>
							
								</tr>
						</c:forEach>
						</tbody>
				</c:if>
				<c:if test="${empty assuntoManifestacao.assuntosPorCategoria }">
					<div align="center">Não há assuntos cadastrados para a categoria selecionada.</div>
				</c:if>
			</table>
		
		</h:form>
	
		
	</f:view>


<script type="text/javascript">
<!--
	function changeLista(categoria) {

		var divPesquisa = jQuery("#assuntosPorCategoria");
		
		
		if (categoria == null) {
			valorIniCategoria = "${assuntoManifestacao.obj.categoriaAssuntoManifestacao.id}";
			if (valorIniCategoria == 0) {
				divPesquisa.hide();
			}			
			else {
				divPesquisa.show();
			}	
		}
		else {
			if (categoria.value == 0) {
				divPesquisa.hide();
			}			
			else {
				divPesquisa.show();
			}
		}
	}
	changeLista(null);
-->
</script>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>