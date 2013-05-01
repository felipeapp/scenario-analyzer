<%@include file="/include/basic.jsp" %>
<f:view>
<%@include file="/include/head_administracao.jsp" %>
<h2>Tipo de Campos extras de Produtos</h2>
<div class="descricaoOperacao">
	Quando um produto � cadastrado pode-se incluir campos de informa��o que ser�o mostrados ao consumidor ao visualiza-los.<br />
</div>
<h:form>
	<ecommerce:Mensagens />
	<table class="formulario">
		<caption>Dados do Tipo de campo extra</caption>
		<tbody>
			<tr>
				<th class="obrigatorio">
					Denomina��o:
				</th>
				<td>
					<h:inputText size="70" value="#{tipoCampoExtraProdutoMBean.obj.denominacao}"/>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">
					Descri��o:
				</th>
				<td>
					<h:inputTextarea cols="80" rows="6" value="#{tipoCampoExtraProdutoMBean.obj.descricao}"/>
				</td>
			</tr>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton value="Cadastrar" action="#{tipoCampoExtraProdutoMBean.cadastrarNovoTipoCampo}"/>
					<h:commandButton value="Cancelar" action="#{tipoCampoExtraProdutoMBean.cancelar}"/>
				</td>
			</tr>
		</tfoot>
	</table>
	
	<br />
	
	<c:if test="${not empty tipoCampoExtraProdutoMBean.allTiposCamposExtrasProduto}">
		<div class="legenda">
			<h:graphicImage url="/img/lupa.gif" width="16" height="16"/>: Visualizar
			<h:graphicImage url="/img/lixeira.png" width="16" height="16"/>: Remover
		</div>
		
		<table class="listagem">
			<caption>Tipos de campos extras</caption>
			<thead>
				<tr>
					<td>Denomina��o</td>
					<td>Descri��o</td>
					<td></td>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="#{tipoCampoExtraProdutoMBean.allTiposCamposExtrasProduto}" var="tipoCampo" varStatus="status">
					<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td>
							<h:outputText value="#{tipoCampo.denominacao}"/>
						</td>
						<td>
							<h:outputText value="#{tipoCampo.descricao}"/>
						</td>
						<td>
							<h:commandLink action="#{tipoCampoExtraProdutoMBean.visualizarDetalhadamente}">
								<h:graphicImage url="/img/lupa.gif" width="16" height="16"/>
								<f:param name="idTipoCampoExtraProduto" value="#{tipoCampo.id}"/>
							</h:commandLink>
							<h:commandLink action="#{tipoCampoExtraProdutoMBean.remover}">
								<h:graphicImage url="/img/lixeira.png" width="16" height="16"/>
								<f:param name="idTipoCampoExtraProduto" value="#{tipoCampo.id}"/>
							</h:commandLink>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:if>
</h:form>
<%@include file="/include/tail_administracao.jsp" %>	
</f:view>