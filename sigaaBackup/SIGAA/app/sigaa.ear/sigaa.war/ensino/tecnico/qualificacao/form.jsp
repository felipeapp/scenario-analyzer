<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
<a4j:keepAlive beanName="qualificacaoTecnicoMBean"/>
<h2> <ufrn:subSistema /> > Cadastro de Qualifica��es</h2>
	
<div class="descricaoOperacao">
	O certificados ser�o emitido baseados nas qualifica��es cadastradas para um curso.<br>
	Uma qualifica��o representa um conjunto de M�dulos que o discente do ensino t�cnico deve
	completar para ter direito a receber certificados de conclus�o dos mesmos.
</div>
	
	
<h:form id="form">
	<table class="formulario" style="width: 90%">
	  <caption>Cadastrar Qualifica��o</caption>
		<h:inputHidden value="#{qualificacaoTecnicoMBean.obj.id}" />
			<tr>
				<th width="25%" class="obrigatorio">Curso:</th>
				<td colspan="2">
					<h:selectOneMenu value="#{qualificacaoTecnicoMBean.obj.cursoTecnico.id}" id="cursoTecnico">
						<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
						<f:selectItems value="#{cursoTecnicoMBean.allCursoComModulo}" />
					</h:selectOneMenu>
					<ufrn:help>
						Aqui s�o listados apenas os curso que possuam estrutura curricular ativa.
					</ufrn:help>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Descri��o:</th>
				<td colspan="2"><h:inputText value="#{qualificacaoTecnicoMBean.obj.descricao}" size="50" onkeyup="CAPS(this)"/></td>
			</tr>
			<tr>
				<th class="obrigatorio">Habilita��o:</th>
				<td>
					<h:selectOneRadio value="#{qualificacaoTecnicoMBean.obj.habilitacao }" id="separador">
						<f:selectItems value="#{qualificacaoTecnicoMBean.simNao}"/>
					</h:selectOneRadio>
				</td>
				<td width="550px">	
					<ufrn:help>
						Caso o certificado dessa qualifica��o deva informar que ela permite o discente ser "Habilitado" em
						determinada �rea referente aos m�dulos.
					</ufrn:help>
				</td>
			</tr>
			<tr>
				<th>Texto para Certificado:</th>
				<td colspan="2">
					<h:inputTextarea value="#{qualificacaoTecnicoMBean.obj.textoCertificado}" rows="10" cols="80" />
				</td>
			</tr>
		  <tfoot>
			   <tr>
					<td colspan="3">
						<h:commandButton value="Cancelar" action="#{qualificacaoTecnicoMBean.cancelar}" onclick="#{confirm}" id="cancelar" />
						<h:commandButton value="avan�ar >>" action="#{qualificacaoTecnicoMBean.submeterDadosBasicos}" id="cadastrar" />
					</td>
			   </tr>
			</tfoot>
  </table>
  
  	<br />
	<center>
		<h:graphicImage url="/img/required.gif" style="vertical-align: top;" />
		<span class="fontePequena"> Campos de preenchimento obrigat�rio. </span>
	</center>
  
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>