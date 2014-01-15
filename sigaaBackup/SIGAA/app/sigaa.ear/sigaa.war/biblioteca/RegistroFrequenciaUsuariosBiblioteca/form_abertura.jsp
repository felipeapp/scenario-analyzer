<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<script type="text/javascript" src="/shared/javascript/prototype-1.6.0.3.js"> </script>

<style>
	.estiloMsgInformacao {
		color: green; 
		list-style-type: disc; 
		font-weight: bold;
	}
	
	.estiloMsgErro {
		color: red; 
		list-style-type: disc; 
		font-weight: bold;
	}
	
	.espacoColunasCabecalho {
		width: 205px;
		border: none;
	}
	
</style>

<h2 class="tituloPagina"> <ufrn:subSistema/> &gt; Cadastrar movimentações diárias dos usuários </h2>

<f:view>

	

	<a4j:keepAlive beanName="registroFrequenciaUsuariosBibliotecaMBean" />

	<h:form id="formulario">
	
		<table class="formulario" width=80%>
			<caption> Selecione o mês e ano de referência </caption>
			<tbody>
					<tr>
						<th class="required">
							Mês da frequência: 
							
						</th>
						<td>
							<h:inputText value="#{registroFrequenciaUsuariosBibliotecaMBean.mesInicio}" size="5" maxlength="2" 
									onkeypress="return ApenasNumeros(event);"/>
						</td>
						
					</tr>
					
					<tr>
						<th class="required">
							Ano da frequência: 
						</th>
						<td>
							<h:inputText value="#{registroFrequenciaUsuariosBibliotecaMBean.ano}" size="5" maxlength="4" 
								onkeypress="return ApenasNumeros(event);"/>
						</td>
					</tr>
					
					<tr>
						<th class="required"> Biblioteca:</th>
						
						<td>	
							<h:selectOneMenu id="comBoxBibliotecasCadastroFrequenciaUsuario" value="#{registroFrequenciaUsuariosBibliotecaMBean.bibliotecaPesquisa.id}">
								<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1" />
								<f:selectItems value="#{registroFrequenciaUsuariosBibliotecaMBean.bibliotecasAtivasRegistroFrenquencia}"/>
							</h:selectOneMenu>
						</td>
					</tr>
					
			</tbody>
			
			<tfoot>
				<tr>
					<td colspan="2" align="center">
						<h:commandButton value="Consultar" action="#{registroFrequenciaUsuariosBibliotecaMBean.consultar}" />
						<h:commandButton value="Cancelar" action="#{registroFrequenciaUsuariosBibliotecaMBean.cancelar}" onclick="#{confirm}"/>
					</td>
				</tr>
			</tfoot>
			
		</table>
	</h:form>
	<br/>
	
	<div class="obrigatorio">Campos de preenchimento obrigatório.</div>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>