<%@include file="/public/include/cabecalho.jsp" %>

<f:view>
	<a4j:keepAlive beanName="relatorioNovasAquisicoesMBean" />
	
	<h2>
		 Novas Aquisições
	</h2>
	
	<div class="descricaoOperacao"> 
		<p>Caro usuário, a partir deste relatório é possível obter as informações dos materiais mais recentes que foram adquiridos 
			e se encontram disponíveis no acervo.</p>
	</div>
	
	<h:form id="form">
		<table id="tableDadosPesquisa" class="formulario" width="55%" style="margin-bottom: 20px">
			<caption>Selecione os campos para a busca</caption>
			<tbody>
				<tr>
					<th width="25%">
						Biblioteca(s):
					</th>
			
					<td width="75%" colspan="6">
						<table>
							<tr>
								<td>
									<h:selectManyListbox value="#{relatorioNovasAquisicoesMBean.bibliotecasID}" style="width:450px;"
											id="smlbxBibliotecas" size="7" title="Várias bibliotecas">
										<f:selectItems value="#{relatorioNovasAquisicoesMBean.bibliotecasCombo}"/>
									</h:selectManyListbox>
								</td>
								<td style="vertical-align: middle; text-align: left; width: 100%;">
									<ufrn:help>
										Mantenha a tecla <em>Ctrl</em> pressionada para selecionar mais de uma biblioteca.<br/>
										Use a tecla <em>Shift</em> para selecionar um intervalo de bibliotecas.<br/><br/>
										Caso não seja selecionada nenhuma biblioteca, serão recuperados os materiais de todas as bibliotecas.
									</ufrn:help>
								</td>
							</tr>
						</table>
					</td>
				</tr>
	
				<tr>
					<th width="25%">
						Área:
					</th>
			
					<td width="75%" colspan="6">
						<h:selectOneMenu id="somAreaCNPQ" value="#{relatorioNovasAquisicoesMBean.areaCNPQID}">
							<f:selectItem itemValue="-1" itemLabel="Todas"/>
							<f:selectItems value="#{relatorioNovasAquisicoesMBean.areasCNPQCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
	
				<tr>
					<th colspan="1" width="25%">
						Período:
					</th>
					<td colspan="1" width="75%">
						<table>
							<tr>
								<td>A partir de</td>
								<td>
									<t:inputCalendar id="inicio" value="#{relatorioNovasAquisicoesMBean.inicioPeriodo}" renderAsPopup="true"
										popupDateFormat="dd/MM/yyyy" onkeypress="return formataData(this,event)"
										renderPopupButtonAsImage="true" size="10" maxlength="10" />
									<ufrn:help> O período máximo permitido na consulta é os <span style="font-weight: bold;">6 últimos meses</span>. </ufrn:help>
								</td>
								
							</tr>
						</table>
					</td>
				</tr>

			</tbody>
	
			<tfoot>
				<tr>
					<td colspan="7">		
						<h:commandButton id="cmdButtonPesquisar" value="Pesquisar" style="margin-right: 10px" actionListener="#{relatorioNovasAquisicoesMBean.pesquisarNovasAquisicoes}" onclick="ativaBotaoFalso();"/>
						
						<%-- Botao falso que é mostrado ao usuário desabilitado, porque não dá para desabilitar o botão geral, senão a ação não é executada --%>
						<h:commandButton id="cmdButtonFakePesquisar" value="Pesquisar" style="display: none; margin-right: 10px" disabled="true" />
						<span id="indicatorGeracaoRelatorio"  style="display: none;"> <h:graphicImage value="/img/indicator.gif" /> </span>
						
						<h:commandButton value="Limpar" actionListener="#{relatorioNovasAquisicoesMBean.limpar}" style="margin-right: 10px"/>
						
						<h:commandButton value="Cancelar" action="#{relatorioNovasAquisicoesMBean.cancelar}"  immediate="true" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
		</table>
		
		<%@include file="/public/include/voltar.jsp" %>
	</h:form>
</f:view>

<script type="text/javascript">

	function ativaBotaoFalso() {
		$('form:cmdButtonPesquisar').hide();
		$('form:cmdButtonFakePesquisar').show();
		$('indicatorGeracaoRelatorio').style.display = '';
	}

	ativaBotaoVerdadeiro();
	
	function ativaBotaoVerdadeiro() {
		$('form:cmdButtonPesquisar').show();
		$('form:cmdButtonFakePesquisar').hide();
		$('indicatorGeracaoRelatorio').style.display = 'none';
	}
	
</script>

<%@include file="/public/include/rodape.jsp" %>