<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

<h2>  <ufrn:subSistema /> &gt; Editar Informações Materiais</h2>

<div class="descricaoOperacao"> 
   <p><b>ATENÇÃO</b>: O tamanho máximo do campo Motivo de baixa é 300 caracteres.</p>
</div>

<style type="text/css">
	
	.codigoBarras{
		width: 30%;
	}
	
	.campoEdicao{
		width: 69%;
	}

	.replicar{
		width: 1%;
	}

</style>

<f:view>

	<a4j:outputPanel ajaxRendered="true" >

		<h:form id="formAlteraDadosVariosMateriais">
		
			<a4j:keepAlive beanName="alterarMotivoBaixaVariosMateriaisMBean"></a4j:keepAlive> 
	
			<%-- Caso o usuário deseja voltar para a tela de pesquisa --%>
			<%-- <a4j:keepAlive beanName="pesquisaTituloCatalograficoMBean"></a4j:keepAlive> --%>
			
			<%-- Caso o usuário deseja voltar para a tela de pesquisa --%>
			<a4j:keepAlive beanName="pesquisaMateriaisInformacionaisMBean"></a4j:keepAlive>
	
			<%-- <c:set var="_titulo" value="${alterarMotivoBaixaVariosMateriaisMBean.titulo}"/>
			<%@include file="/public/biblioteca/informacoes_padrao_titulo.jsp"%> --%>
						
			<table class="formulario" width="80%">
				<caption> Títulos Selecionados ( ${fn:length(alterarMotivoBaixaVariosMateriaisMBean.titulosMateriais) } )</caption>
							
				<thead>		
					<tr><th style="text-align: left; width: 30%">Informações</th></tr>
				</thead>
				
				<tbody>
					<c:forEach items="${alterarMotivoBaixaVariosMateriaisMBean.titulosMateriais}" var="tituloMaterial" varStatus="row">
						<tr class="${row.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
							<td>${tituloMaterial}</td>
						</tr> 
					</c:forEach>
				</tbody>
			</table>
					
	
	
			<div class="infoAltRem" style="margin-top: 10px; width: 80%">
	
				<h:graphicImage value="/img/arrow_down.png" style="overflow: visible;" />: 
				Copiar o valor do campo para os demais campos abaixo dele
			</div>
	
	
	
			<table class="formulario" width="80%">
	
				<caption> Materiais Selecionados ( ${fn:length(alterarMotivoBaixaVariosMateriaisMBean.materiaisSelecionados) } )</caption>
							
				<thead>
					
					<th style="text-align: left; width: 30%">Código de Barras do Material</th>
					
					 
					<th style="text-align: left">Motivo de baixa</th>
					 
					
					<th style="width: 1%"> </th>
					
				</thead>
			</table>
	
	
	
	
	
			<t:dataTable var="material" rowIndexVar="index" value="#{alterarMotivoBaixaVariosMateriaisMBean.materiaisSelecionados}" 
			  	columnClasses="codigoBarras, campoEdicao, replicar" rowClasses="linhaPar, linhaImpar" style="width:80%; margin-left:auto; margin-right:auto; border:1px solid #DEDFE3; ">
				
				<t:column>
					<h:outputText id="outputCodigoBarras" value="#{material.codigoBarras}" />
				</t:column>
				 
				<t:column>
					<h:inputTextarea id="inputAreaMotivoBaixa" value="#{ material.motivoBaixa }" cols="57" rows="2"  />
					<ufrn:help>Razão pela qual foi dado baixa no material.</ufrn:help>
				</t:column> 

				<t:column>
					<a4j:commandLink actionListener="#{alterarMotivoBaixaVariosMateriaisMBean.copiaValorCamposAbaixo}" reRender="formAlteraDadosVariosMateriais">
						<h:graphicImage url="/img/arrow_down.png" style="border:none"
							title="Clique aqui copiar o valor deste campo para os demais campos abaixo" />

						<f:param name="idMaterialOrigemDado" value="#{material.id}"/>					
					</a4j:commandLink>
				</t:column>			
			</t:dataTable>
	
	
	
	
	
	
	
			<table class="formulario" width="80%">
				
				<tfoot>
					<tr>				
						<td colspan="9" style="text-align: center;">
							<h:commandButton id="cmdButtonFinalizarAlteracao" value="Finalizar Alteração" action="#{alterarMotivoBaixaVariosMateriaisMBean.realizarAlteracaoMateriais}" onclick="ativaBotaoFalso();">
								 <f:setPropertyActionListener target="#{alterarMotivoBaixaVariosMateriaisMBean.finalizarAlteracao}" value="true" />
							</h:commandButton>
							
							<h:commandButton id="cmdButtonFakeFinalizarAlteracao" value="Finalizar Alteração" style="display: none;" disabled="true" />
							
							<h:commandButton id="cmdButtonSalvar" value="Salvar"  action="#{alterarMotivoBaixaVariosMateriaisMBean.realizarAlteracaoMateriais}" onclick="ativaBotaoFalso();">
								<f:setPropertyActionListener target="#{alterarMotivoBaixaVariosMateriaisMBean.finalizarAlteracao}" value="false" />
							</h:commandButton>
							
							<h:commandButton id="cmdButtonFakeSalvar" value="Salvar" style="display: none;" disabled="true" />
							
							<h:commandButton value="<< Voltar"  action="#{alterarMotivoBaixaVariosMateriaisMBean.telaPaginaBuscaMateriais}"/>
							<h:commandButton value="Cancelar"  action="#{alterarMotivoBaixaVariosMateriaisMBean.cancelar}" onclick="#{confirm}" immediate="true"/>
						</td>
					</tr>
				</tfoot>
				
			</table>
	
	
	

		
	
		</h:form>
		
	</a4j:outputPanel>
	
</f:view>

<script type="text/javascript">

	function ativaBotaoFalso() {
		$('formAlteraDadosVariosMateriais:cmdButtonFinalizarAlteracao').hide();
		$('formAlteraDadosVariosMateriais:cmdButtonSalvar').hide();
		$('formAlteraDadosVariosMateriais:cmdButtonFakeFinalizarAlteracao').show();
		$('formAlteraDadosVariosMateriais:cmdButtonFakeSalvar').show();
	}

	ativaBotaoVerdadeiro();
	
	function ativaBotaoVerdadeiro() {
		$('formAlteraDadosVariosMateriais:cmdButtonFinalizarAlteracao').show();
		$('formAlteraDadosVariosMateriais:cmdButtonSalvar').show();
		$('formAlteraDadosVariosMateriais:cmdButtonFakeFinalizarAlteracao').hide();
		$('formAlteraDadosVariosMateriais:cmdButtonFakeSalvar').hide();
	}
	
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>