<%@include file="/ava/cabecalho.jsp" %>

<script type="text/javascript">
function toggleAula() {
	if ($('topicoAula').style.display == 'none') { 
	    $('topicoAula').style.display = '';
	} else {
	    $('topicoAula').style.display = 'none';
	}	
}	


</script>

<f:view>
	<%@include file="/ava/menu.jsp" %>

	<a4j:keepAlive beanName="forumTurmaBean" />
	<h:form id="form" enctype="multipart/form-data">
		<fieldset>
			<legend><h:outputText value="#{ forumTurmaBean.confirmButton }" /> F�rum</legend>

			<ul class="form">
				<%@include file="/ava/Foruns/_form.jsp" %>
				
				<c:if test="${turmaVirtual.docente  || permissaoAva.permissaoUsuario.forum}">
				
					<li id="topicoAula">
						<label>T�pico de Aula:</label>
						<h:selectOneMenu id="aula" value="#{ forumTurmaBean.obj.topicoAula.id }" rendered="#{ not empty topicoAula.comboIdentado }">
							<f:selectItem itemValue="0" itemLabel=" -- Nenhum t�pico de aula selecionado -- "/>
							<f:selectItems value="#{ topicoAula.comboIdentado }" />
						</h:selectOneMenu>
						<h:selectOneMenu id="sem-aula" value="#{ forumTurmaBean.obj.topicoAula.id }" styleClass="sem-topicos-aula" rendered="#{ empty topicoAula.comboIdentado }">
							<f:selectItem itemLabel="Nenhum T�pico de Aula foi cadastrado" itemValue="0"/>
						</h:selectOneMenu>
						<ufrn:help>Selecione um t�pico de aula para exibir este f�rum na p�gina inicial da turma virtual.</ufrn:help>
					</li>
				</c:if>
				
			</ul>
			
			<div class="botoes">
				<div class="form-actions">
					<h:commandButton action="#{ forumTurmaBean.cadastrar }" value="#{ forumTurmaBean.confirmButton }" id="btnSalvar"/>
				</div>
				<div class="other-actions" style="width: 20%">					
					<h:commandButton action="#{ forumTurmaBean.listar }" value="<< Voltar"/>
					<h:commandButton action="#{ forumTurmaBean.cancelar }" value="Cancelar" immediate="true" id="btnCancelar"  onclick="#{confirm}"/> 
				</div>
				<div class="required-items">
					<span class="required"></span>Campos de Preenchimento Obrigat�rio
				</div>
			</div>
		
		</fieldset>
	
	</h:form>
</f:view>
<%@include file="/ava/rodape.jsp" %>
