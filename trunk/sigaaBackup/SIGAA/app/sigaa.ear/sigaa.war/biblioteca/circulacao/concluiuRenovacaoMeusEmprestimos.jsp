<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<h:form id="formConcluiuRenovacaoMeusEmprestimos">
	
	<h2><ufrn:subSistema /> &gt; Conclus�o da Renova��o <i>Online</i> dos Empr�stimos</h2>
	
	<div class="descricaoOperacao" style="width:90%;">
		<p>A renova��o dos empr�stimos abaixo foi conclu�da com sucesso. Um email de confirma��o foi enviado para a sua caixa postal.</p>
		<p><strong>Importante: </strong>Para evitar transtornos guarde ou imprima o comprovante de renova��o ou uma c�pia dessa p�gina at� que se tenha certeza que os empr�stimos foram renovados. 
		Para verificar as renova��es, emita o seu hist�rico de empr�stimos ou verifique o recebimento do email de confirma��o da renova��o.</p>
		<br/>
		<p><strong>Observa��o: </strong> N�o ser�o aceitas reclama��es posteriores caso n�o se tenha o comprovante da renova��o.</p>
	</div>
	
	<a4j:keepAlive beanName="meusEmprestimosBibliotecaMBean" />

	<%-- Passa alguns par�metros no request para depois ter como verificar nos log por onde o usu�rio passou se ele vim afirmar que renovou os empr�timos --%>
	<h:inputHidden id="inputHiddenMensagemPaginaConfirma" value="!!! USU�RIO SUBMETEU A P�GINA DE CONCLUS�O DAS RENOVA��ES DOS EMPR�STIMOS !!!"></h:inputHidden>
	<h:inputHidden id="idsEmprestimosRenovados" value="#{meusEmprestimosBibliotecaMBean.idsEmprestimoRenovados}"></h:inputHidden>

		<%--  Parte onde o usu�rio visualiza o comprovante da renova��o  --%>
		<c:if test="${meusEmprestimosBibliotecaMBean.habilitarComprovante}">
		
			
				<table  class="subFormulario" align="center">
					<caption style="text-align: center;">Impress�o Comprovante</caption>
					<tr>
					<td width="8%" valign="middle" align="center">
						<html:img page="/img/warning.gif"/>
					</td>
					<td valign="middle" style="text-align: justify">
						Por favor, para uma maior seguran�a imprima o comprovante da renova��o clicando no link ao lado.
					</td>
					<td>
						<table>
							<tr>
								<td align="center">
							 		<h:graphicImage url="/img/printer_ok.png" />
							 	</td>
							 </tr>
							 <tr>
							 	<td style="font-size: medium;">
							 		<h:commandLink title="Imprimir Comprovante"  target="_blank" value="COMPROVANTE" action="#{meusEmprestimosBibliotecaMBean.geraComprovanteRenovacao}"  />
							 	</td>
							 </tr>
						</table>
					</td>
					</tr>
				</table>
			<br/>
			
			
			
			
			<div id="divComprovanteRenovacao" style="width: 80%; margin-left: auto; margin-right: auto; margin-top: 20px; border: 1px black solid;">
				
				<h3 style="text-align: center;  margin-top: 20px;">COMPROVANTE DE RENOVA��O</h3>
				
				<div id="divDadosUsuario">
			
					<c:if test="${meusEmprestimosBibliotecaMBean.infoUsuario != null }">
						
						<c:set var="_infoUsuarioCirculacao" value="${meusEmprestimosBibliotecaMBean.infoUsuario}" scope="request" />
						<c:set var="_transparente" value="true" scope="request" />
						<c:set var="_mostrarFoto" value="false" scope="request" />			
									
						<%@include file="/biblioteca/circulacao/dadosUsuarioBiblioteca.jsp"%>
						
					</c:if>
			
				</div>
				
				
				<div id="divMensagemDeclaracao" style="text-align: center; margin-top: 20px; margin-bottom: 30px;">
					As renova��es dos materiais abaixo foram realizadas com sucesso:
				</div>	
				
				<div id="dadosMaterial">
		
					<table style="width: 100%;"> 
		
						<thead style="background-color: transparent;">
							<tr>
								<td style="width: 20%; font-weight: bold; text-align: left;">Data da Renova��o</td>
								<td style="width: 60%; font-weight: bold; text-align: center;">Informa��es do Material</td>
								<td style="width: 20%; font-weight: bold; text-align: left;">Prazo para Devolu��o</td>
							</tr>
						</thead>
					
						<c:forEach var="operacaoRenovacao" items="#{meusEmprestimosBibliotecaMBean.emprestimosRenovadosOp}" varStatus="status">
							<tr>
								<td style="text-align: left;"> ${operacaoRenovacao.dataRealizacaoFormatada} </td>
								<td style="text-align: left; padding-left: 10px;"> ${operacaoRenovacao.infoMaterial} </td>
								<td style="text-align: left;"> ${operacaoRenovacao.prazoFormatado} </td>
							</tr>
							
						</c:forEach>
					</table>
				
				</div>
			
			
				<div id="divCodigoVerificacao" style="margin-top: 30px;  margin-bottom: 30px; text-align: center; color: gray; font-style: italic;">
						C�digo de Autentica��o : ${meusEmprestimosBibliotecaMBean.codigoAutenticacaoRenovacao}
				</div>
				
			</div>
		
		</c:if>	

	</h:form>

</f:view>


<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>