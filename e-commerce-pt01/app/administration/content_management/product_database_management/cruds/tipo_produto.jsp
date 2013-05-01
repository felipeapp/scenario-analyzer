<%@include file="/include/basic.jsp" %>
<f:view>
<%@include file="/include/head_administracao.jsp" %>
<h2>Tipo de Produtos</h2>
<div class="descricaoOperacao">
	Esta opera��o permite o cadastro, modifica��o e remor��o de tipos de produtos cadastrados.<br />
	Tipos de Produtos s�o tipos b�sicos, que separa, por exemplo, produtos f�sicos (um livro, uma caneta), de um servi�o 
	(Como um determinado suporte ao usu�rio).
</div>
<h:form>
	<ecommerce:Mensagens />
	<table class="formulario">
		<caption>Dados do Tipo</caption>
		<tbody>
			<tr>
				<th class="obrigatorio">
					Denomina��o:
				</th>
				<td>
					<h:inputText size="70" value="#{tipoProdutoMBean.tipo.denominacao}"/>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">
					Descri��o:
				</th>
				<td>
					<h:inputTextarea cols="80" rows="6" value="#{tipoProdutoMBean.tipo.descricao}"/>
				</td>
			</tr>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton value="Cadastrar" action="#{tipoProdutoMBean.cadastrarNovoTipo}"/>
					<h:commandButton value="Cancelar" action="#{tipoProdutoMBean.cancelar}"/>
				</td>
			</tr>
		</tfoot>
	</table>
	
	<br />
	
	<c:if test="${not empty tipoProdutoMBean.allTiposProduto}">
		<div class="legenda">
			<h:graphicImage url="/img/lupa.gif" width="16" height="16"/>: Visualizar
			<h:graphicImage url="/img/lixeira.png" width="16" height="16"/>: Remover
		</div>
		
		<table class="listagem">
			<caption>Tipos de Produtos Dispon�veis</caption>
			<thead>
				<tr>
					<td>Denomina��o</td>
					<td>Descri��o</td>
					<td>Data de Cadastro</td>
					<td></td>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="#{tipoProdutoMBean.allTiposProduto}" var="tipo" varStatus="status">
					<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td>
							${tipo.denominacao}
						</td>
						<td>
							${tipo.descricao}
						</td>
						<td>
							<h:outputText value="#{tipo.dataCadastro}">
								<f:convertDateTime pattern="dd/MM/yyyy"/>
							</h:outputText>
						</td>
						<td>
							<h:commandLink action="#{tipoProdutoMBean.visualizarDetalhadamenteTipo}">
								<h:graphicImage url="/img/lupa.gif" width="16" height="16"/>
								<f:param name="idTipoProduto" value="#{tipo.id}"/>
							</h:commandLink>
							<h:commandLink action="#{tipoProdutoMBean.removerTipo}">
								<h:graphicImage url="/img/lixeira.png" width="16" height="16"/>
								<f:param name="idTipoProduto" value="#{tipo.id}"/>
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