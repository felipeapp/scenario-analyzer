<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2>  <ufrn:subSistema /> &gt; Confirmar Remoção do Fascículo </h2>

<c:set var="confirmRemocao" value="if (!confirm('Confirma remoção do fascículo do acervo ?')) return false" scope="request" />

<div class="descricaoOperacao"> 
	<p>A operação de remoção deve ser realizada para fascículos que <strong>nunca existiram no acervo</strong>, foram incluído por engano.</p>
	<p>O fascículo removido sairá totalmente do sistema, podendo inclusive ser inserido um outro fascículo com o mesmo código de barras.</p>
</div>

<f:view>

	<h:form>
		<%-- Quando edita um fascículo a partir da página de pesquisa --%>
		<a4j:keepAlive beanName="pesquisarFasciculoMBean"></a4j:keepAlive>

		<a4j:keepAlive beanName="editaMaterialInformacionalMBean"></a4j:keepAlive>

		<c:set var="_titulo" value="${editaMaterialInformacionalMBean.titulo}" scope="request"/>
		<%@include file="/public/biblioteca/informacoes_padrao_titulo.jsp"%>
	
		<c:set var="_assinatura" value="${editaMaterialInformacionalMBean.fasciculoEdicao.assinatura}" scope="request"/>
		<%@include file="/biblioteca/info_assinatura.jsp"%>
	
	
		<table class="formulario" width="100%" style="maring-top:20px">
					
			<tbody>
				
				<caption> Dados Fascículo </caption>
				
				<tr>
					<th>Código de Barras: </th> 
					<td>
						<h:outputText id="inputTextCodigoBarras"  value="#{editaMaterialInformacionalMBean.fasciculoEdicao.codigoBarras}" />
					</td>
				</tr>
				
				<tr>
					<th width="25%">Ano Cronológico: </th> 
					<td>
						<h:outputText id="inputTextAnoCronologico" value="#{editaMaterialInformacionalMBean.fasciculoEdicao.anoCronologico}" />
					</td>
				</tr>
				
				<tr>
					<th>Dia/Mês:</th>
					<td>
						<h:outputText id="inputTextDiaMes" value="#{editaMaterialInformacionalMBean.fasciculoEdicao.diaMes}"/>
					</td>
				</tr>
				
				<tr>
					<th>Ano:</th>
					<td>
						<h:outputText id="inputTextAno" value="#{editaMaterialInformacionalMBean.fasciculoEdicao.ano}" />
					</td>
				</tr>
				
				<tr>
					<th>Volume:</th>
					<td>
						<h:outputText id="inputTextVolume" value="#{editaMaterialInformacionalMBean.fasciculoEdicao.volume}"/>
					</td>
				</tr>
				
				<tr>
					<th>Número:</th>
					<td>
						<h:outputText id="inputTextNumero" value="#{editaMaterialInformacionalMBean.fasciculoEdicao.numero}" />
					</td>
				</tr>
				
				<tr>
					<th>Edição:</th>
					<td>
						<h:outputText id="inputTextEdicao" value="#{editaMaterialInformacionalMBean.fasciculoEdicao.edicao}" />
					</td>
				</tr>
				
				<tr>
					<th>Número de Chamada (localização)</th>
					<td colspan="3">
						<h:outputText id="inputTextNumeroChamada" value="#{ editaMaterialInformacionalMBean.fasciculoEdicao.numeroChamada }"  />
					</td>
				</tr>
				
				<tr>
					<th>Segunda Localização</th>
					<td colspan="3">
						<h:outputText id="inputTextSegundaLocalizacao" value="#{ editaMaterialInformacionalMBean.fasciculoEdicao.segundaLocalizacao }" />
					</td>
				</tr>
				
				
				<tr>
					<th>Biblioteca:</th>
					<td colspan="3"> 
						<h:outputText id="comboboxBiblioteca" value="#{editaMaterialInformacionalMBean.fasciculoEdicao.biblioteca.descricao}"/> 
					</td>
				</tr>
				
				
				<tr>
					<th >Coleção:</th>
					<td colspan="3">
						<h:outputText id="comboboxColecao" value="#{editaMaterialInformacionalMBean.fasciculoEdicao.colecao.descricao}"/> 
					</td>
				</tr>
				
				<tr>
				
					<th>Situação:</th>
					<td>
						<h:outputText id="comboboxSitucao" value="#{editaMaterialInformacionalMBean.fasciculoEdicao.situacao.descricao}"/> 
					</td>
				</tr>
				
				<tr>
					<th>Status:</th>
					<td>
					<h:outputText id="comboboxStatus" value="#{editaMaterialInformacionalMBean.fasciculoEdicao.status.descricao}"/> 		
					</td>
				</tr>
				
				<tr>
					<th>Tipo Material:</th>
					<td colspan="3">
						<h:outputText id="comboboxTipoMaterial" value="#{editaMaterialInformacionalMBean.fasciculoEdicao.tipoMaterial.descricao}"/>
					</td>
		
				</tr>
				
				<tr>
					<th>Nota Geral:</th>
					<td colspan="3">
						<h:outputText id="inputAreaNotaGeral" value="#{ editaMaterialInformacionalMBean.fasciculoEdicao.notaGeral }" />
					</td>
		
				</tr>
				
				<tr>
					<th>Nota ao Usuário:</th>
					<td colspan="3">
						<h:outputText id="inputAreaNotaUsuario" value="#{ editaMaterialInformacionalMBean.fasciculoEdicao.notaUsuario }"  />
					</td>
		
				</tr>
				
				
				<tr>
					<th>Suplemento que acompanha o fascículo:</th>
					<td colspan="3">
						<h:outputText id="inputAreaSuplemento" value="#{ editaMaterialInformacionalMBean.fasciculoEdicao.descricaoSuplemento }"/>
					</td>
				</tr>
				
				
				<tfoot>
					<tr>
						<td colspan="4" align="center">
						
							<h:commandButton id="botaoRemoverMaterial"  value="Remover Fascículo " action="#{editaMaterialInformacionalMBean.apagarMaterial}" onclick="#{confirmRemocao}"/>
							
							<h:commandButton value="<< Voltar" action="#{editaMaterialInformacionalMBean.voltarPaginaFasciculo}" rendered="true" id="botaoVoltar" />
							
							<h:commandButton value="Cancelar" action="#{editaMaterialInformacionalMBean.cancelar}" onclick="#{confirm}" immediate="true" id="cancelar" />						
						</td>
					</tr>
				</tfoot>
				
				
		</table>


	</h:form>


</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>