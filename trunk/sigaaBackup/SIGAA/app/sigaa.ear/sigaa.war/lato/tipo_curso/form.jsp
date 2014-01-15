<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.dominio.ModalidadeEducacao"%>

<f:view>
<h2><ufrn:subSistema /> &gt; ${tipoCursoLatoMBean.confirmButton} Tipo de Curso</h2>

<h:form id="form">

  <table class=formulario width="60%">
   <caption class="listagem">${tipoCursoLatoMBean.confirmButton} Tipo de Curso</caption>
		<h:inputHidden value="#{tipoCursoLatoMBean.obj.id}"/>
		<h:inputHidden value="#{tipoCursoLatoMBean.obj.ativo}"/>
		<tr>
			<th class="obrigatorio">Descrição:</th>
			<td><h:inputText value="#{tipoCursoLatoMBean.obj.descricao}" maxlength="30" size="40" id="descricao" /></td>
		</tr>
		<tr>
			<th class="obrigatorio">Ch Mínima:</th>
			<td><h:inputText value="#{tipoCursoLatoMBean.obj.chMinima}" maxlength="3" size="4" id="chMinima" 
				onkeyup="return formatarInteiro(this);"/>h</td>
		</tr>
		<tr>
			<th>Ch Máxima:</th>
			<td><h:inputText value="#{tipoCursoLatoMBean.obj.chMaxima}" maxlength="3" size="4" id="chMaxima" 
				onkeyup="return formatarInteiro(this);"/>h</td>
		</tr>
		  <tr>
	  		<tfoot>
			   <tr>
					<td colspan="2">
						<h:commandButton value="#{tipoCursoLatoMBean.confirmButton}" action="#{tipoCursoLatoMBean.cadastrar}" id="cadastrar" />
						<h:commandButton value="Cancelar" action="#{tipoCursoLatoMBean.cancelar}" immediate="true" onclick="#{confirm}" id="cancelar" />
					</td>
			   </tr>
		</tfoot>
   </table>
</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>