<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<a4j:keepAlive beanName="buscaUsuarioBibliotecaMBean" />
	<a4j:keepAlive beanName="comunicarMaterialPerdidoMBean" />

	<h:form id="formDevolverMaterialPerdido">
		<h2><ufrn:subSistema /> &gt; Comunicar Material Perdido &gt; Devolver Empréstimo</h2>

		<div class="descricaoOperacao">
			<p>Caro usuário,</p>
			<p>Utilize essa operação para realizar a devolução do material perdido pelo usuário.</p>
			<br/>
			<p>O empréstimo será devolvido e o material perdido será baixado no acervo, porque uma vez que o usuário comunicou a perda do material,
			ele não pode mais entrega o mesmo material, deve obrigatoriamente substituir por outro.</p>
			<br/>
			<p>Existem três opção de devolução:
				<ul>
					<li><strong>Usuário Repos um Material Similar:</strong> Fluxo padrão da operação, o usuário entregou um material similar ao perdido, ele possuirá o mesmo código de barras do anterior.</li>
					<li><strong>Usuário Repos um Material Equivalente:</strong> O usuário entregou um material equivalente ao perdido, deve-se realizar a baixa do material perdido e incluir um novo material no acervo.</li>
					<li><strong>Usuário Não Repos o Material:</strong> Utilize essa operação quando a pendência do usuário na biblioteca deve ser retirada por motivo de força maior, apesar do usuário não ter reposto o material perdido. 
					Isso implica em perda financeira para instituíção. </li>
				</ul>
			</p>
			
		</div>
		
		
		<table class="visualizacao" style="width: 90%;">
			<caption>Empréstimo Selecionado</caption>
			
			<tr>
				<td colspan="4">
					<c:set var="_infoUsuarioCirculacao" value="${comunicarMaterialPerdidoMBean.informacaoUsuario}" scope="request" />
					<c:set var="_mostrarFoto" value="${false}" scope="request" />
					<%@ include file="/biblioteca/circulacao/dadosUsuarioBiblioteca.jsp"%>
				</td>
			</tr>
			
			<tr>
				<th>Código de Barras:</th>
				<td>
					${comunicarMaterialPerdidoMBean.emprestimo.material.codigoBarras}
				</td>
				<th>Material:</th>
				<td>
					${comunicarMaterialPerdidoMBean.emprestimo.material.informacao}
				</td>
			</tr>
			
			<tr>
				<th style="width: 20%;">Data de Empréstimo:</th>
				<td>
					<ufrn:format type="data" valor="${comunicarMaterialPerdidoMBean.emprestimo.dataEmprestimo}" />
				</td>
				
				<th style="width: 20%;">Prazo do Empréstimo:</th>
				<td>
					<ufrn:format type="dataHora" valor="${comunicarMaterialPerdidoMBean.emprestimo.prazo}" />
				</td>
			</tr>
			
			<tr>
								
				<td colspan="4">
					<table class="subFormulario" style="width: 90%; ">
						<caption style="background-color: #C8D5EC">Comunicações de Perdas Realizadas para o Emprétimo</caption>
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
									Prazo para reposição:
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
	        				<f:selectItem itemLabel="Usuário Repôs um Material Similar" itemValue="#{comunicarMaterialPerdidoMBean.valorTipoDevolucaoUsuarioEntregouSimilar}" /> 
	        				<f:selectItem itemLabel="Usuário Repôs um Material Equivalente" itemValue="#{comunicarMaterialPerdidoMBean.valorTipoDevolucaoUsuarioEntregouEquivalente}" />   
	        				<f:selectItem itemLabel="Usuário Não Repôs o Material" itemValue="#{comunicarMaterialPerdidoMBean.valorTipoDevolucaoUsuarioNaoEntregou}" />
	    						<a4j:support event="onclick" actionListener="#{comunicarMaterialPerdidoMBean.verificaAlteracaoTipoDevolucao}" reRender="formDevolverMaterialPerdido"/>
	    				</h:selectOneRadio>
    				</div>
				</td>
			</tr>
			
			
			<tr>
				<c:if test="${comunicarMaterialPerdidoMBean.valorTipoDevolucao == comunicarMaterialPerdidoMBean.valorTipoDevolucaoUsuarioEntregouSimilar}">
					<th colspan="4" style="text-align: center; color: green; height: 40px;">Material ${comunicarMaterialPerdidoMBean.emprestimo.material.codigoBarras} ficará disponível no acervo.</th>
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
					<td colspan="2" class="obrigatorio" style="font-weight: normal;">Motivo da não entrega do material: </td>
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
						<h:commandButton value="Devolver Empréstimo sem Baixar Material" action="#{comunicarMaterialPerdidoMBean.devolverEmprestimoSemBaixarMaterial}" onclick="return confirm('Confirma a devolução do empréstimo do material perdido sem baixar o material ? ');" />
						
						<h:commandButton value="Devolver Empréstimo Baixando o Material" action="#{comunicarMaterialPerdidoMBean.devolverEmprestimoBaixandoMaterial}" onclick="return confirm('Confirma a devolução do empréstimo do material perdido baixando o material ? ');" />
						
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