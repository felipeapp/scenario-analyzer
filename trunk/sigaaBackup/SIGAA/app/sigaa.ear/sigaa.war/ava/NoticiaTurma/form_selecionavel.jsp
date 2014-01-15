<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<a4j:keepAlive beanName="noticiaTurmaSelecionavelBean" />
	<h:messages showDetail="true"></h:messages>
	<h2 class="title"><ufrn:subSistema /> &gt; Cadastro de Notícia para uma turma</h2>

	<h:form id="form">
		<h:inputHidden value="#{noticiaTurmaSelecionavelBean.obj.turma.id}"/>

		<table class="formulario" style="width: 95%;">
			<caption>Formulário de Cadastro de Notícia</caption>
			<tr> 
				<td width="1%"></td>
				<th width="10%" >Turma:</th>
				<td><b>${noticiaTurmaSelecionavelBean.obj.turma.nome}</b></td>
			</tr>
			<tr>
				<td></td>
				<th class="required">
					<label for="form:criarEm">Criar em: </label>
				</th>
				<td>
				<h:selectBooleanCheckbox  id="topicosAula" onclick="marcarTodos(this)"/>
					<t:selectManyCheckbox id="criarEm" value="#{ noticiaTurmaSelecionavelBean.cadastrarEm }" layout="pageDirection" escape="false">
						<t:selectItems var="ts" itemLabel="#{ ts.descricaoComDocentes }" itemValue="#{ ts.id }" value="#{ noticiaTurmaSelecionavelBean.turmasMesmaDisciplinaSemestre }"/>
					</t:selectManyCheckbox>
				</td>
			</tr>
			<tr>
				<td></td>
				<th class="required">
					<label for="form:titulo">Título: </label>
				</th>
				<td>
					<h:inputText maxlength="200" value="#{noticiaTurmaSelecionavelBean.obj.descricao}" id="titulo" style="width: 95%"/>
				</td>
			</tr>
			<tr>
				<td></td>
				<th class="required">
					<label for="form:texto">Texto: </label>
				</th>
				<td>
					<h:inputTextarea id="noticia" value="#{noticiaTurmaSelecionavelBean.obj.noticia}" style="width: 98%" rows="10"/>
				</td>
			</tr>
			<tr>
				<td colspan="2"></td>
				<td>
					<h:selectBooleanCheckbox value="#{noticiaTurmaSelecionavelBean.enviarEmail}" id="checkEmail"/>
					<h:outputLabel for="checkEmail">
						<strong>Enviar notificação por e-mail</strong>
					</h:outputLabel>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton value="Cadastrar Notícia" action="#{noticiaTurmaSelecionavelBean.cadastrar}" id="btnCadastrar"/>
						<h:commandButton value="<< Voltar" action="#{noticiaTurmaSelecionavelBean.voltarTurma}" />
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{noticiaTurmaSelecionavelBean.cancelar}" id="btnCancelar" immediate="true"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<div class="obrigatorio"> Campos de preenchimento obrigatório. </div>


	<c:if test="${not empty noticiaTurmaSelecionavelBean.noticias}">
		<br />
		<table class="listagem" style="width: 95%">
			<caption>Notícias cadastradas para a turma</caption>
			<thead>
				<tr>
					<th width="20%"> Data</th>
					<th> Título </th>
					<th width="15%"> Autor </th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="_noticia" items="#{noticiaTurmaSelecionavelBean.noticias}" varStatus="status">
				<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
					<td><ufrn:format type="dataHora" valor="${_noticia.data}" /></td>
					<td>${_noticia.descricao}</td>
					<td><em>${_noticia.usuarioCadastro.login}</em></td>
				</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:if>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

<script src="/shared/javascript/tiny_mce/tiny_mce.js" type="text/javascript"></script>
<script type="text/javascript">
tinyMCE.init({
	mode : "textareas", theme : "advanced", width : "680", height : "250", language : "pt",
	theme_advanced_buttons1 : "cut,copy,paste,separator,search,replace,separator,bold,italic,underline,separator,strikethrough,justifyleft,justifycenter,justifyright,justifyfull,separator,bullist,numlist,image",
	theme_advanced_buttons2 : "fontselect,fontsizeselect,separator,undo,redo,separator,forecolor,backcolor,link,separator,sub,sup,charmap",
	theme_advanced_buttons3 : "",
	plugins : "searchreplace,contextmenu,advimage",
	theme_advanced_toolbar_location : "top",
	theme_advanced_toolbar_align : "left"
});
</script>

<script type="text/javascript">

function marcarTodos(elem) {

	jQuery("#form\\:criarEm").find("input").each(
			function(index,item) {
				if (!marcar)
					item.checked = true;
				else
					item.checked = false;
			}
	);

	if (marcar) {
		marcar = false;
		jQuery(elem).val(false);
	}	
	else {
		marcar = true;
		jQuery(elem).val(true);
	}	
	
}
var marcar = false;
</script>