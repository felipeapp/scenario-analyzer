<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<h2> <ufrn:subSistema /> > Altera��o de Registro de Diplomas </h2>

<h:form>
<a4j:keepAlive beanName="alteracaoRegistroDiplomaMBean"></a4j:keepAlive>

<div class="descricaoOperacao">
<p>Caro usu�rio,</p>
<p>Este caso de uso permite a altera��o de dados em Registros de Diplomas. Preencha o formul�rio abaixo com muita aten��o.</p>
<p>Valores incorretos implicar�o na impress�o de diplomas com valores incorretos.</p>
</div>

<table class="formulario" width="90%">
	<caption>Dados do Registro</caption>
	<tbody>
		<tr>
			<th class="rotulo">Discente:</th>
			<td>
				<h:outputText value="#{alteracaoRegistroDiplomaMBean.registroAntigo.discente.matriculaNome}"/>
			</td>
		</tr>
		<tr>
			<th class="rotulo"> Livro: </th>
			<td>
				<h:outputText value="#{alteracaoRegistroDiplomaMBean.registroAntigo.livroRegistroDiploma.titulo} - #{alteracaoRegistroDiplomaMBean.registroAntigo.discente.curso}"/>
			</td>
		</tr>
		<tr>
			<th class="rotulo"> Folha: </th>
			<td>
				<h:outputText value="#{alteracaoRegistroDiplomaMBean.registroAntigo.folha.numeroFolha}"/>
			</td>
		</tr>
		<tr>
			<th class="rotulo"> N� do Registro: </th>
			<td>
				<h:outputText value="#{alteracaoRegistroDiplomaMBean.registroAntigo.numeroRegistro}" />
			</td>
		</tr>
		<tr>
			<th class="rotulo">
				<c:choose>
				<c:when test="${alteracaoRegistroDiplomaMBean.registroNovo.discente.graduacao}">Data da Cola��o:</c:when>
				<c:otherwise>Data da Homologa��o:</c:otherwise>
				</c:choose>
			</th>
			<td>
				<ufrn:format type="dia_mes_ano" valor="${alteracaoRegistroDiplomaMBean.registroNovo.dataColacao}" />
				<ufrn:help>Para alterar a data de cola��o do Registro de Diploma, altere a dada de Conclus�o do discente.</ufrn:help>
			</td>
		</tr>
		<tr>
			<td colspan="2" class="subFormulario">
				Dados a Serem Alterados
			</td>
		</tr>
		<tr>
			<th> N�mero do Protocolo: </th>
			<td><h:inputText value="#{alteracaoRegistroDiplomaMBean.registroNovo.processo}" onkeyup="formatarProtocolo(this, event);" size="22" maxlength="20" id="processo2" /><br>
				(ex.: 23077.001234/2003-98) Caso n&atilde;o saiba os digitos verificadores, informe 99
			</td>
		</tr>
		<tr>
			<th class="required"> Data do Registro: </th>
			<td ><t:inputCalendar value="#{alteracaoRegistroDiplomaMBean.registroNovo.dataRegistro}" size="10" maxlength="10"
				 onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy" id="dataRegistro" 
				 renderAsPopup="true" renderPopupButtonAsImage="true">
				 	<f:converter converterId="convertData"/>
				</t:inputCalendar>
			</td>
		</tr>
		<tr>
			<th class="required"> Data de Expedi��o: </th>
			<td ><t:inputCalendar value="#{alteracaoRegistroDiplomaMBean.registroNovo.dataExpedicao}" size="10" maxlength="10"
				 onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy" id="dataExpedicao" 
				 renderAsPopup="true" renderPopupButtonAsImage="true">
				 	<f:converter converterId="convertData"/>
				</t:inputCalendar>
			</td>
		</tr>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="2" align="center">
				<h:commandButton action="#{alteracaoRegistroDiplomaMBean.cadastrar}" value="Atualizar" id="atualizar"/>
				<h:commandButton action="#{alteracaoRegistroDiplomaMBean.cancelar}" value="Cancelar" onclick="#{confirm}" id="cancelar"/>
			</td>
		</tr>
	</tfoot>
</table>
<br>
<center>
	<html:img page="/img/required.gif" style="vertical-align: top;" /> 
	<span class="fontePequena">Campos de preenchimento obrigat�rio. </span> 
</center>
</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>