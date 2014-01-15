<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<a4j:keepAlive beanName="buscaUsuarioBibliotecaMBean" />
	<a4j:keepAlive beanName="comunicarMaterialPerdidoMBean" />

	<h:form id="formDevolverMaterialPerdido">
		<h2><ufrn:subSistema /> &gt; Comunicar Material Perdido &gt; Devolver Empr�stimo</h2>

		<div class="descricaoOperacao">
			<p>Caro usu�rio,</p>
			<p>Utilize essa opera��o para realizar a devolu��o do material perdido pelo usu�rio.</p>
			<br/>
			<p>O empr�stimo ser� devolvido e o material perdido ser� baixado no acervo, porque uma vez que o usu�rio comunicou a perda do material,
			ele n�o pode mais entrega o mesmo material, deve obrigatoriamente substituir por outro.</p>
			<br/>
			<p>Existem tr�s op��o de devolu��o:
				<ul>
					<li><strong>Usu�rio Repos um Material Similar:</strong> Fluxo padr�o da opera��o, o usu�rio entregou um material similar ao perdido, ele possuir� o mesmo c�digo de barras do anterior.</li>
					<li><strong>Usu�rio Repos um Material Equivalente:</strong> O usu�rio entregou um material equivalente ao perdido, deve-se realizar a baixa do material perdido e incluir um novo material no acervo.</li>
					<li><strong>Usu�rio N�o Repos o Material:</strong> Utilize essa opera��o quando a pend�ncia do usu�rio na biblioteca deve ser retirada por motivo de for�a maior, apesar do usu�rio n�o ter reposto o material perdido. 
					Isso implica em perda financeira para institu���o. </li>
				</ul>
			</p>
			
		</div>
		
		
		<table class="visualizacao" style="width: 90%;">
			<caption>Empr�stimo Selecionado</caption>
			
			<tr>
				<td colspan="4">
					<c:set var="_infoUsuarioCirculacao" value="${comunicarMaterialPerdidoMBean.informacaoUsuario}" scope="request" />
					<c:set var="_mostrarFoto" value="${false}" scope="request" />
					<%@ include file="/biblioteca/circulacao/dadosUsuarioBiblioteca.jsp"%>
				</td>
			</tr>
			
			<tr>
				<th>C�digo de Barras:</th>
				<td>
					${comunicarMaterialPerdidoMBean.emprestimo.material.codigoBarras}
				</td>
				<th>Material:</th>
				<td>
					${comunicarMaterialPerdidoMBean.emprestimo.material.informacao}
				</td>
			</tr>
			
			<tr>
				<th style="width: 20%;">Data de Empr�stimo:</th>
				<td>
					<ufrn:format type="data" valor="${comunicarMaterialPerdidoMBean.emprestimo.dataEmprestimo}" />
				</td>
				
				<th style="width: 20%;">Prazo do Empr�stimo:</th>
				<td>
					<ufrn:format type="dataHora" valor="${comunicarMaterialPerdidoMBean.emprestimo.prazo}" />
				</td>
			</tr>
			
			<tr>
								
				<td colspan="4">
					<table class="subFormulario" style="width: 90%; ">
						<caption style="background-color: #C8D5EC">Comunica��es de Perdas Realizadas para o Empr�timo</caption>
						<tbody  style="background: transparent;">
							<c:forEach items="#{comunicarMaterialPerdidoMBean.emprestimo.prorrogacoes}" var="p">
								<tr>
									<th style="font-weight: bold; width: 15%;">
									Prazo anterior:
									</th>
									<td style="width: 10%;">
										<ufrn:format type="data" valor="${p.dataAnterior}" /> 
									</td>
									<th style="font-weight: bold; width: 20%;">
									Prazo para reposi��o:
									</th>
									<td style="width: 10%;">
										<ufrn:format type="data" valor="${p.dataAtual}" /> 
									</td>
									<td style="width: 35%" ></td>
								</tr>
								<tr>
									<th style="font-weight: bold; width: 10%">
									Justificativa:
									</th>
									<td colspan="5">
										${p.motivo}
									</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</td>
				
			</tr>
		
			<tr>
				<td colspan="4">
					<div style="text-align: center; width: 90%; margin: auto;">
						<h:selectOneRadio value="#{comunicarMaterialPerdidoMBean.valorTipoDevolucao}">  
	        				<f:selectItem itemLabel="Usu�rio Rep�s um Material Similar" itemValue="#{comunicarMaterialPerdidoMBean.valorTipoDevolucaoUsuarioEntregouSimilar}" /> 
	        				<f:selectItem itemLabel="Usu�rio Rep�s um Material Equivalente" itemValue="#{comunicarMaterialPerdidoMBean.valorTipoDevolucaoUsuarioEntregouEquivalente}" />   
	        				<f:selectItem itemLabel="Usu�rio N�o Rep�s o Material" itemValue="#{comunicarMaterialPerdidoMBean.valorTipoDevolucaoUsuarioNaoEntregou}" />
	    						<a4j:support event="onclick" actionListener="#{comunicarMaterialPerdidoMBean.verificaAlteracaoTipoDevolucao}" reRender="formDevolverMaterialPerdido"/>
	    				</h:selectOneRadio>
    				</div>
				</td>
			</tr>
			
			
			<tr>
				<c:if test="${comunicarMaterialPerdidoMBean.valorTipoDevolucao == comunicarMaterialPerdidoMBean.valorTipoDevolucaoUsuarioEntregouSimilar}">
					<th colspan="4" style="text-align: center; color: green; height: 40px;">Material ${comunicarMaterialPerdidoMBean.emprestimo.material.codigoBarras} ficar� dispon�vel no acervo.</th>
				</c:if>
				<c:if test="${comunicarMaterialPerdidoMBean.valorTipoDevolucao == comunicarMaterialPerdidoMBean.valorTipoDevolucaoUsuarioEntregouEquivalente}">
					<th colspan="4" style="text-align: center; color: red; height: 40px;">Material ${comunicarMaterialPerdidoMBean.emprestimo.material.codigoBarras} deve ser baixado do acervo.</th>
				</c:if>
				<c:if test="${comunicarMaterialPerdidoMBean.valorTipoDevolucao == comunicarMaterialPerdidoMBean.valorTipoDevolucaoUsuarioNaoEntregou}">
					<th colspan="4" style="text-align: center; color: red; height: 40px;">Material ${comunicarMaterialPerdidoMBean.emprestimo.material.codigoBarras} deve ser baixado do acervo.</th>
				</c:if>
			</tr>
			
			
			<c:if test="${comunicarMaterialPerdidoMBean.valorTipoDevolucao == comunicarMaterialPerdidoMBean.valorTipoDevolucaoUsuarioNaoEntregou}">
				<tr>
					<td colspan="2" class="obrigatorio" style="font-weight: normal;">Motivo da n�o entrega do material: </td>
					<td  colspan="2" style="text-align: center;">
						<h:inputTextarea value="#{comunicarMaterialPerdidoMBean.motivoNaoEntregaMaterial}" cols="80" rows="3"
								onkeyup="textCounter(this, 'quantidadeCaracteresDigitados', 200);" />
					</td>
				</tr>
				<tr>
					<td colspan="3">
					<td style="text-align: center;">
						Caracteres Restantes: <span id="quantidadeCaracteresDigitados">200</span>
					</td>
				</tr>
			</c:if>
		
		
			<tfoot>
				<tr>
					<td colspan="4">
						<h:commandButton value="Devolver Empr�stimo sem Baixar Material" action="#{comunicarMaterialPerdidoMBean.devolverEmprestimoSemBaixarMaterial}" onclick="return confirm('Confirma a devolu��o do empr�stimo do material perdido sem baixar o material ? ');" />
						
						<h:commandButton value="Devolver Empr�stimo Baixando o Material" action="#{comunicarMaterialPerdidoMBean.devolverEmprestimoBaixandoMaterial}" onclick="return confirm('Confirma a devolu��o do empr�stimo do material perdido baixando o material ? ');" />
						
						<h:commandButton value="Cancelar" action="#{comunicarMaterialPerdidoMBean.telaListaEmprestimosAtivoUsuarioParaComunicarMaterialPerdido}" immediate="true" onclick="#{confirm}" />
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