<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	
	<a4j:keepAlive beanName="relacionaClassificacaoBibliograficaBibliotecasMBean"></a4j:keepAlive>
	
	<h2><ufrn:subSistema /> &gt; Bibliotecas e Suas Classificações Bibliográficas </h2>

	<div class="descricaoOperacao"> 
	    <p> Caro Usuário, </p>
	    <p> Nesta página é possível configurar as classificações bibliográficas utilizadas em cada biblioteca. </p>
	    <p> Com essa informação é possível, no momento da inclusão no acervo de um material informacional, validar se a catalogação do material
	    está com a classificação utilizada na biblioteca. Impedindo-se que materiais fiquem sem classificação no sistema.
	    </p>
	</div>
	
	

	<h:form id="formConfiguraRelacionamentoBibliotecaClassificacoes">

		<table class="formulario" width="95%">
			<caption class="listagem">Bibliotecas e suas Classificações</caption>
			<thead>
				<tr>
					<th>Biblioteca</th>
					<th>Classificação Utilizada</th>
				</tr>
			</thead>
			
			
			<c:forEach items="#{relacionaClassificacaoBibliograficaBibliotecasMBean.relacionamentosConfigurados}" var="relacionamento" varStatus="status">
				<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					
					<td>${relacionamento.biblioteca.descricao}</td>
					
					<td>
						<h:selectOneMenu id="comboBoxClassificacao1" value="#{relacionamento.classificacao.id}">
							<f:selectItem itemLabel="-- Selecione --" itemValue="-1" />
							<f:selectItems value="#{relacionaClassificacaoBibliograficaBibliotecasMBean.allClassificacoesCombo}"/>
						</h:selectOneMenu>
					</td>
					
				</tr>
			</c:forEach>
			
			
			<tfoot>
				<tr>
					<td colspan="4" style="text-align: center;">
						<h:commandButton value="Atualizar" action="#{relacionaClassificacaoBibliograficaBibliotecasMBean.atualizarRelacionamentos}" 
							onclick="return confirm('Confirma a atualização dos relacionamentos ? ');"/>
						<h:commandButton value="Cancelar" action="#{relacionaClassificacaoBibliograficaBibliotecasMBean.cancelar}" immediate="true" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
			
		</table>

	</h:form>

</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>