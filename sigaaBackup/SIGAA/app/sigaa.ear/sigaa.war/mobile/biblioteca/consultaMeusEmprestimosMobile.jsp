
<%@include file="/mobile/commons/cabecalho.jsp"%>


	<f:view>
	
		<h:form>
			
			<h:commandButton value="Menu Principal" action="#{operacoesBibliotecaMobileMBean.telaMenuPrincipalBibliotecaMobile}"/> <br/><br/>
			
				<table class="formularioMobile">
						<caption> Campos de Busca </caption>
						
						<tbody>
						
							<tr>
								<th colspan="6" class="required">Data Inicial:</th>
							</tr>
							<tr>
								<td colspan="1" > <h:inputText value="#{operacoesBibliotecaMobileMBean.diaInicial}" size="3" maxlength="2"/>  </td>
								<td colspan="1" >  <c:out value="/" />  </td>
								<td colspan="1" > <h:inputText value="#{operacoesBibliotecaMobileMBean.mesInicial}" size="3" maxlength="2"/> </td>
								<td colspan="1" >  <c:out value="/" />  </td>
								<td colspan="2" > <h:inputText value="#{operacoesBibliotecaMobileMBean.anoInicial}" size="5" maxlength="4"/> </td>
							</tr>
							
							<tr>
								<th colspan="6" class="required">Data Final:</th>
							</tr>		
							<tr>
								<td colspan="1" > <h:inputText value="#{operacoesBibliotecaMobileMBean.diaFinal}" size="3" maxlength="2"/>  </td>
								<td colspan="1" > <c:out value="/" />  </td>
								<td colspan="1" > <h:inputText value="#{operacoesBibliotecaMobileMBean.mesFinal}" size="3" maxlength="2"/>  </td>
								<td colspan="1" >  <c:out value="/" /> </td>
								<td colspan="2" > <h:inputText value="#{operacoesBibliotecaMobileMBean.anoFinal}" size="5" maxlength="4"/>  </td>
							</tr>		
						
						</tbody>
						
						<tfoot>
							<tr>
								<td colspan="6" align="center">
									<h:commandButton value="Buscar" action="#{operacoesBibliotecaMobileMBean.consultaUltimosEmprestimosUsuario}" />
								</td>
							</tr>
						</tfoot>
				</table>
			
				<div class="obrigatorio" style="width: 170px;">Campos de preenchimento obrigatório.</div>
				
			</h:form>
	</f:view>




<%@include file="/mobile/commons/rodape.jsp" %>