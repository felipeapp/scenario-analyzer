<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<h2><ufrn:subSistema/> > Gera��o de Arquivo do ENADE</h2>
<div class="descricaoOperacao">
<p>Caro Usu�rio,</p>
		<p>Por favor, informe a lista de matr�culas dos discentes que
			ser�o inscritos no ENADE e informe qual o separador utilizado na
			lista de matr�culas: V�RGULA, PONTO E V�RGULA, ESPA�O ou TABULA��O.</p>
		<p>Ser� gerado um arquivo com os dados para realizar a inscri��o dos alunos no ENADE.</p>
	</div>
<br>
	<h:form id="formulario">
		<table class="formulario" >
			<caption>Matr�culas</caption>
			<tr>
				<th class="required">
					Tipo de discente:
				</th>
				<td>
					<h:selectOneMenu value="#{gerarArquivoENADE.tipo}" id="tipo">
						<f:selectItem itemValue="Ingressante"/>
						<f:selectItem itemValue="Concluinte"/>
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="required">
					Separador Utilizado:
				</th>
				<td>
					<h:selectOneMenu value="#{gerarArquivoENADE.separador}"  id="separador">
						<f:selectItems value="#{ gerarArquivoENADE.separadoresCombo }"/>
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="required" valign="top">
					Matr�culas para Gera��o do Arquivo:
				</th>
				<td>
					<h:inputTextarea value="#{gerarArquivoENADE.matriculas}" rows="20" cols="80" id="matriculas"/>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2">
					 	<h:commandButton action="#{gerarArquivoENADE.gerarArquivo}" value="Gerar Arquivo" id="btnArquivos"/>
					 	<h:commandButton action="#{gerarArquivoENADE.cancelar}" value="Cancelar" onclick="#{ confirm }" id="btnCancelar"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
</f:view>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>