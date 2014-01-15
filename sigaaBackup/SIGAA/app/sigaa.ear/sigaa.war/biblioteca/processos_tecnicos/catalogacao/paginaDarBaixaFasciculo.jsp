<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2>  <ufrn:subSistema /> &gt; Confirma Baixa do Fascículo </h2>

<c:set var="confirmBaixa" value="if (!confirm('Confirma a baixa do fascículo ?')) return false" scope="request" />

<div class="descricaoOperacao">
	<p>A operação de baixa deve ser realizada para fascículos que existiram algum dia no acervo, mas que, por algum motivo,
	não têm mais condições de serem consultados pelo usuário.</p>
	<p>Se a baixa do fascículo for realizada, ele não aparecerá mais nas consultas do acervo para o usuário final,
	mas ele ainda constará no sistema. Será possível visualizá-lo pela página de pesquisa de fascículos e no relatório
	de materiais baixados.</p>
</div>

<f:view>

	<h:form>

		<%-- Quando edita um fascículo a partir da página de pesquisa --%>
		<a4j:keepAlive beanName="pesquisarFasciculoMBean"></a4j:keepAlive>
		
		<a4j:keepAlive beanName="editaMaterialInformacionalMBean"></a4j:keepAlive>
			
		<table class="formulario" width="100%" style="maring-top:20px">
					
			<caption> Dar Baixa Fascículo </caption>
			
			<tbody>
			
				<tr>
					<td>	
						<c:set var="_titulo" value="${editaMaterialInformacionalMBean.titulo}" scope="request"/>
						<%@include file="/public/biblioteca/informacoes_padrao_titulo.jsp"%>
					</td>
				</tr>
				
				<tr>
					<td>
						<c:set var="_assinatura" value="${editaMaterialInformacionalMBean.fasciculoEdicao.assinatura}" scope="request"/>
						<%@include file="/biblioteca/info_assinatura.jsp"%>
					</td>
				</tr>
				
				<tr>
					<td>
						<table class="subFormulario" width="100%">
							<caption> Dados do Fascículo </caption>
							
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
							
							
						
							<tr>
								<th class="obrigatorio">Motivo da Baixa:</th>
								<td colspan="3">
									<h:inputTextarea id="inputAreaMotivoBaixa" value="#{ editaMaterialInformacionalMBean.fasciculoEdicao.motivoBaixa }" 
										cols="80" rows="5"  onkeyup="textCounter(this, 'quantidadeCaracteresDigitados', 300);" />
								</td>
					
							</tr>
							<tr>
								<td>
								<td colspan="3" style="text-align: center;">
									Caracteres Restantes: <span id="quantidadeCaracteresDigitados">300</span>
								</td>
							</tr>
							
						</table>
					</td>
				</tr>
				
			</tbody>
				
			<tfoot>
				<tr>
					<td colspan="4" align="center">
					
						<h:commandButton id="botaoDarBaixaMaterial"  value="Dar Baixa no Fascículo " action="#{editaMaterialInformacionalMBean.darBaixaMaterial}"  onclick="#{confirmBaixa}" />
						<h:commandButton value="<< Voltar" action="#{editaMaterialInformacionalMBean.voltarPaginaFasciculo}" rendered="true" id="botaoVoltar" />
						
						<h:commandButton value="Cancelar" action="#{editaMaterialInformacionalMBean.cancelar}" onclick="#{confirm}" immediate="true" id="cancelar" />						
					</td>
				</tr>
			</tfoot>
				
		</table>
		
	</h:form>
		
</f:view>


<script type="text/javascript">
 
function textCounter(field, idMostraQuantidadeUsuario, maxlimit) {
	
	if (field.value.length > maxlimit){
		field.value = field.value.substring(0, maxlimit);
	}else{ 
		document.getElementById(idMostraQuantidadeUsuario).innerHTML = maxlimit - field.value.length ;
	} 
}

</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>