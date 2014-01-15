<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>


<c:set var="confirmBloqueio" value="if (!confirm('Confirma o bloqueio do Usu�rio?')) return false" scope="request" />
<c:set var="confirmDesbloqueio" value="if (!confirm('Confirma o desbloqueio do Usu�rio?')) return false" scope="request" />

<style>
table.subFormulario tr.nomePessoa td{
		background: #EEEEEE;
		font-weight: bold;
		padding-left: 20px;
	}
</style>


<h2> <ufrn:subSistema /> &gt; Bloquear Usu�rios para Empr�stimos no Sistema</h2>

<div class="descricaoOperacao"> 
   <p>
   		<strong>Aten��o: </strong> 
   		Caso o usu�rio seja bloqueado ele n�o poder� mais realizar empr�stimos no sistema, mesmo 
   		que ele possua um v�nculo que permita isso.
   </p>
</div>

<f:view>

	<a4j:keepAlive beanName="buscaUsuarioBibliotecaMBean" />
	<a4j:keepAlive beanName="bloquearUsuarioBibliotecaMBean" />

	<h:form id="formulario">

		<c:set var="_infoUsuarioCirculacao" value="${bloquearUsuarioBibliotecaMBean.infoUsuario}" scope="request" />
		<c:set var="_inativo" value="${bloquearUsuarioBibliotecaMBean.usuarioEstaBloqueado}" scope="request" />
		<c:set var="_mostrarVinculo" value="false" scope="request" />
		<%@include file="/biblioteca/circulacao/dadosUsuarioBiblioteca.jsp"%>
	
		
	
		<table class="formulario" style="width: 90%;margin-top: 10px;" >
			
			<caption> Altere o status do Usu�rio em rela��o aos empr�stimos no sistema </caption>
			
			<tbody>
			
				<c:if test="${fn:length(bloquearUsuarioBibliotecaMBean.bloqueiosUsuario) > 0 }">
				
					<tr>
						<td colspan="2" style="padding-bottom: 10px;">
							<table class="subFormulario" style="width: 100%;" >
								
								<caption> Hist�rico de Bloqueios/Desbloqueios do Usu�rio </caption>
								
								<thead>
									<tr>
										<th>Opera��o</th>
										<th>Data</th>
										<th>Motivo</th>
										<th>Realizado pelo Usu�rio</th>
									</tr>
								</thead>
								
								<tbody>
									<c:forEach var="historico" items="#{bloquearUsuarioBibliotecaMBean.bloqueiosUsuario}"  varStatus="status">
										<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
											
											<c:if test="${historico.tipo == historico.bloqueio}">
												<td style="width: 10%; color: red;">Bloqueado</td>
											</c:if>
											
											<c:if test="${historico.tipo == historico.desbloqueio}">
												<td style="width: 10%; color: blue;">Desbloqueado</td>
											</c:if>
											
											<td style="width: 15%">
												<ufrn:format type="dataHora" valor="${historico.data}" />
											</td>
											
											<td style="width: 45%">
												${historico.motivo}
											</td>
											
											<td style="width: 20%">
												${historico.usuarioRealizouOperacao.nome}
											</td>
										</tr>
									</c:forEach>
								</tbody>
								
							</table>
						</td>
					</tr>
			
				</c:if>
				
				<tr>
					<td colspan="2" style="text-align: center; font-size: 14px;"> 
						<h:outputText style="color: red;"  value="Usu�rio Est� Bloqueado" rendered="#{bloquearUsuarioBibliotecaMBean.usuarioEstaBloqueado}"/> 
						 <h:outputText style="color: green;" value="Usu�rio N�o Est� Bloqueado" rendered="#{! bloquearUsuarioBibliotecaMBean.usuarioEstaBloqueado}"/>
					</td>
				</tr>
			
				<tr>
					<th class="required"> Motivo:</th>
					<td>
						<t:inputTextarea id="textAreaObservacao" value="#{bloquearUsuarioBibliotecaMBean.motivoBloqueio}"  cols="70" rows="4" onkeyup="textCounter(this, 'quantidadeCaracteresDigitados', 200);"/>
					</td>
				</tr>
				<tr>
					<th>Caracteres Restantes:</th>
					<td>
						<span id="quantidadeCaracteresDigitados">200</span>
					</td>
				</tr>
				
			</tbody>
			
			<tfoot>
				<tr>
					<td colspan="2" style="text-align: center;">
					
						<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO} %>">
						
							<h:commandButton id="cmdButtonBloqueiUsuario" value="Bloquear Usu�rio" action="#{bloquearUsuarioBibliotecaMBean.bloqueiar}" 
										onclick="#{confirmBloqueio}"  rendered="#{! bloquearUsuarioBibliotecaMBean.usuarioEstaBloqueado}" />
							<h:commandButton id="cmdButtonDesBloqueiUsuario" value="Desbloquear Usu�rio" action="#{bloquearUsuarioBibliotecaMBean.bloqueiar}" 
										onclick="#{confirmDesbloqueio}" rendered="#{bloquearUsuarioBibliotecaMBean.usuarioEstaBloqueado}"/>
						</ufrn:checkRole>
						
						<h:commandButton id="cmdButtonVoltar" value="<< Voltar" action="#{bloquearUsuarioBibliotecaMBean.voltarParaBuscaUsuarioBiblioteca}" rendered="#{!bloquearUsuarioBibliotecaMBean.desabilitaBotaoVoltar }"/>			
						
						<h:commandButton id="cancelar"  value="Cancelar" onclick="#{confirm}" immediate="true" action="#{bloquearUsuarioBibliotecaMBean.cancelar}" />
						
					</td>
				</tr>
			</tfoot>
			
			
		</table>
	
	
	</h:form>

</f:view>


<SCRIPT LANGUAGE="JavaScript">
 
function textCounter(field, idMostraQuantidadeUsuario, maxlimit) {
	
	if (field.value.length > maxlimit){
		field.value = field.value.substring(0, maxlimit);
	}else{ 
		document.getElementById(idMostraQuantidadeUsuario).innerHTML = maxlimit - field.value.length ;
	} 
}

</script>



<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>