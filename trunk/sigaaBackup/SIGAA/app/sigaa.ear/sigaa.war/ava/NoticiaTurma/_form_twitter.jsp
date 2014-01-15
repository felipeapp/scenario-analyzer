
<ul class="form">
	<li>
		<table><tr><th style="width:130px;vertical-align:middle;padding-right:10px;vertical-align:top;" class="required">Texto do Twitter:
		</th><td>
			&nbsp;
			<h:inputTextarea id="observacao" value="#{twitterMBean.novoStatus}"  style="width: 300px; height: 50px;"
 			 onkeyup="maxCaracteres(this, 'txtCaracteresDigitados', 130)" onblur="maxCaracteres(this, 'txtCaracteresDigitados', 130)"/>
			<br/>
			<strong>(130 caracteres/<span id="txtCaracteresDigitados">0 digitados</span>)</strong>
		</td></tr></table>
	</li>
	
	<%-- <c:if test="${ not empty twitterMBean.turmasPermissaoTwitter}">	
		<li>
			<label class="normal" for="form:notificar" style="font-weight:normal;padding-right:10px;">Notificação?</label>
			<h:selectBooleanCheckbox value="#{twitterMBean.notificar}" styleClass="noborder" id="notificar"/>
		</li>
	
	
		<li>
			<table>
				<tr>
					<th style="width:130px;vertical-align:middle;padding-right:10px;vertical-align:top;" class="required">Alterar status em:</th>
					<td>
						<t:selectManyCheckbox id="criarEm" value="#{twitterMBean.alterarStatusPara}" layout="pageDirection">
							<t:selectItems var="ts" itemLabel="#{ ts.descricaoSemDocente }" itemValue="#{ ts.id }" value="#{ turmaVirtual.turmasSemestrePermissaoDocente }"/>
						</t:selectManyCheckbox>
					</td>
				</tr>
			</table>
		</li>
	</c:if> --%>
</ul>

<script type="text/javascript">

function marcarTodos(elem) {

	jQuery("#formNoticia\\:criarEm").find("input").each(
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
</script>

	