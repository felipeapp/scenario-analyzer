<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<style>
	#cepIndicator {
		padding: 0 25px;
		color: #999;
	}
	span.info {
		font-size: 0.9em;
		color: #888;
	}
</style>

<f:view>
	
	<h2 class="tituloPagina"> <ufrn:subSistema/> > Residência Universitária</h2>
	
	<h:messages showDetail="true"></h:messages>
	
	<h:form id="form">
		<table class="formulario" width="100%">
		<body>
				
				<tr>
					<td colspan="4">
						<table width="100%" class="subFormulario">
						
							<caption>Dados da Residência</caption>
			
							<tr>
								<th>Localização:</th>
									<td colspan="5">
										<span class="required"></span>
										<h:inputText value="#{residenciaUniversitariaMBean.obj.localizacao}" maxlength="40" size="40" disabled="#{bolsaAuxilioMBean.readOnly}" id="idLocalizacao"/>
									</td>
								</td>
							</tr>
							
							<tr>
								<th>Sexo: <span class="required"></span> </th>
								<td colspan="4">
									<h:selectOneRadio value="#{residenciaUniversitariaMBean.obj.sexo}">
										<f:selectItem itemLabel="Masculino" itemValue="M" />
										<f:selectItem itemLabel="Feminino" itemValue="F" />
										<f:selectItem itemLabel="Mista" itemValue="MF" />
									</h:selectOneRadio>
									
								</td>
							</tr>
							
						</table>
					</td>
				</tr>
				
			</tbody>
			
			<tfoot>
			<tr>
				<td colspan="4" align="center">
					<h:commandButton value="Cadastrar" action="#{residenciaUniversitariaMBean.cadastrar}" id="submeter" />
					<h:commandButton value="Cancelar" action="#{residenciaUniversitariaMBean.cancelar}" id="cancelarOperacao" onclick="#{confirm}" /> 
				</td>
			</tr>
			</tfoot>
			
		</table>
	</h:form>
	
	<br>
	<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
		class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
	<br>
</center>
	

</f:view>


<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>