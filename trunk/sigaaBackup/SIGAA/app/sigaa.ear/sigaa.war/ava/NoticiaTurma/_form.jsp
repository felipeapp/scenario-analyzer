
<table class="formAva">
	<tr>
		<th id="tituloTh" class="required">Título:</th>
		<td>
			<h:inputText maxlength="200" value="#{noticiaTurma.object.descricao}" id="titulo" size="59"/>
		</td>
	</tr>
	
	<tr>
		<th class="required">Texto:</th>
		<td>
			<h:inputTextarea cols="70" rows="5" value="#{noticiaTurma.object.noticia}" id="texto" style="width: 90%;"/>
		</td>
	</tr>
	
	<tr>
		<th id="notificacaoTh">Notificação: </th>
		<td>
			<h:selectBooleanCheckbox id="notificar" value="#{noticiaTurma.notificar}" onclick="mudaNotificar(this);"/>
			<span class="texto-ajuda">(Notificar os alunos por e-mail)</span> 
		</td>
	</tr>
	
	<tr id="tipoTituloEmail">
		<th id="tituloEmailTh">Enviar Email com:</th>
		<td>
			<h:selectOneRadio  id= "tipoTituloEmailSelecionado" value="#{noticiaTurma.tipoTituloNotificacao}" style="display:inline;" layout="pageDirection">
				<f:selectItem itemValue="C" itemLabel = "Título Padrão" />
				<f:selectItem itemValue="P" itemLabel = "Título da Notícia" />
			</h:selectOneRadio>
			<ufrn:help><b>Exemplo de título padrão:</b> Uma notícia foi cadastrada na turma virtual: <i>Nome da Turma.</i></ufrn:help>		
		</td>
	</tr>
	
	<c:if test="${ not empty turmaVirtual.turmasPermissaoNoticia && noticiaTurma.object.id == 0}">	
		<tr>
			<th class="required" width="110px">Criar em:</th>
			<td>
				<label onclick="marcarTodos(this)"><img src="/sigaa/img/check.png"> MARCAR TODOS</label>
				<t:selectManyCheckbox id="criarEm" value="#{ noticiaTurma.cadastrarEm }" layout="pageDirection">
					<t:selectItems var="ts" itemLabel="#{ ts.descricaoSemDocente }" itemValue="#{ ts.id }" value="#{ turmaVirtual.turmasPermissaoNoticia }"/>
				</t:selectManyCheckbox>
			</td>
		</tr>
	</c:if>
	<%--<c:if test="${twitterMBean.verificaTurmaTemTwitter}">	
		<li>
			  <rich:simpleTogglePanel switchType="client" label="Noticia Via Twitter" height="150px">
			 	<ul>
			 		<li>
	                  <table><tr><th style="width:130px;vertical-align:middle;"><b>Texto Twitter:</b>
						</th><td>
							<h:inputTextarea id="observacao" styleClass="mceNoEditor" value="#{twitterMBean.novoStatus}"  style="width: 500px; height: 40px;"
				 			 onkeyup="maxCaracteres(this, 'txtCaracteresDigitados', 140)" onblur="maxCaracteres(this, 'txtCaracteresDigitados', 140)"/>
							<br/>
							<strong>(140 caracteres/<span id="txtCaracteresDigitados">0 digitados</span>)</strong>
						</td></tr></table>
					</li>
					<li>
						<table><tr><th style="width:130px;vertical-align:top;"><b>Alterar Status em:</b> <span class="required">&nbsp;</span>
						</th><td>
							<label onclick="marcarTodos(this)"><img src="/sigaa/img/check.png"> MARCAR TODOS</label>
							<t:selectManyCheckbox id="postarEm" value="#{twitterMBean.alterarStatusPara}" layout="pageDirection">
								<t:selectItems var="ts" itemLabel="#{ ts.descricaoSemDocente }" itemValue="#{ ts.id }" value="#{twitterMBean.turmasPermissaoTwitter}"/>
							</t:selectManyCheckbox>
						</td></tr></table>
					</li>
			 	</ul>
	         </rich:simpleTogglePanel>
		</li>
	</c:if> --%>
</table>

<script type="text/javascript">

function marcarTodos(elem) {

	jQuery("#formAva\\:criarEm").find("input").each(
			function(index,item) {
				if (!marcar)
					item.checked = true;
				else
					item.checked = false;
			}
	);

	if (marcar) {
		marcar = false;
		jQuery(elem).html('<img src="/sigaa/img/check.png"> MARCAR TODOS</label>');
	}	
	else {
		marcar = true;
		jQuery(elem).html('<img src="/sigaa/img/check_cinza.png"> DESMARCAR TODOS</label>');
	}	
	
}
var marcar = false;

function maxCaracteres(e, txtCaracteresDigitados, qtde) {	
	if (e.value.length > qtde) {
		e.value = e.value.substr(0,qtde);
	}
	$(txtCaracteresDigitados).innerHTML = e.value.length + ' digitados';
}

function ActualizarTxt(){
    Ext.get('formAva:observacao').dom.value = Ext.get('formAva:texto').dom.value;
    maxCaracteres(Ext.get('formAva:observacao').dom, 'txtCaracteresDigitados', 140)
}

function mudaNotificar(caixa) {
	if (caixa != null) {
			var tipoTituloEmail = jQuery("#tipoTituloEmail");
			if (caixa.checked) {
				tipoTituloEmail.show();			
			}
			else {
				tipoTituloEmail.hide();		
			}
	}
		
}
</script>
	