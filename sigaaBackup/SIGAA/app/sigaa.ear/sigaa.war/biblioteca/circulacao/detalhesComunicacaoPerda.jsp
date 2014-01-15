
<%-- Pagina que é inserida por ajax na listagem de comunicações para permitir o usuário visualizar os detalhes da comunicação de perda escolhida --%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>


<%@ taglib uri="/tags/sigaaFunctions" prefix="sf" %>
<%@ taglib uri="/tags/ufrn" prefix="ufrn" %>


<c:set var="ctx" value="<%= request.getContextPath() %>"/>



<%--    Apenas chama  o metodo do bean para buscar e popular os dados da comunicação selecionada
        O id do emprestimo e o id do usuário biblioteca foram passados como parâmetros por java script                       
--%>
${comunicarMaterialPerdidoMBean.carregaDadosComunicacao}

<style type="text/css">

.dadosComunicacao tr th{
	text-align: left;
	width: 25%;
	font-weight: bold;  /* não funcina no ie */
}

.dadosComunicacao tr td{
	text-align: left;
}

</style>


<f:view>

	<c:set var="_infoUsuarioCirculacao" value="${comunicarMaterialPerdidoMBean.informacaoUsuario}" scope="request" />
	<c:set var="_transparente" value="true" scope="request" />
	<%@include file="/biblioteca/circulacao/dadosUsuarioBiblioteca.jsp"%>
	
	
	<table class="dadosComunicacao" style="background: none; border: none; width: 100%"> 
		
		<tbody>
			
			<c:forEach items="#{comunicarMaterialPerdidoMBean.emprestimo.prorrogacoes}" var="p"  varStatus="status"  >
								
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
			
					<td colspan="6">
						<table style="width: 100%; ">
							<tbody  style="background: transparent;">
								<tr>
									<th colspan="2" style="text-align: center; font-weight: bold;" >
									Comunicação ${status.index + 1} de ${fn:length(comunicarMaterialPerdidoMBean.emprestimo.prorrogacoes)}: 
									</th>
								</tr>
								<tr>
									<th style="font-weight: bold;">
									Prazo anterior do empréstimo:
									</th>
									<td>
										<ufrn:format type="data" valor="${p.dataAnterior}" /> 
									</td>
								</tr>
								<tr>	
									<th style="font-weight: bold;">
									Prazo para reposição:
									</th>
									<td>
										<ufrn:format type="data" valor="${p.dataAtual}" /> 
									</td>
								</tr>
								<tr>
									<th style="font-weight: bold;">
									Justificativa:
									</th>
									<td style="width: 80%;">
										${p.motivo}
									</td>
								</tr>
								<tr>
									<th style="font-weight: bold;">
									Cadastrado por:
									</th>
									<td>
										${p.nomeUsuarioCriou}
									</td>
								</tr>
							</tbody>
						</table>
					</td>
				</tr>
			</c:forEach>
			
		</tbody>
				
	</table>

</f:view>

