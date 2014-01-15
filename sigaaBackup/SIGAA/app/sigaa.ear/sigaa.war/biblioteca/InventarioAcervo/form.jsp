<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<script type="text/javascript">

	function limparCampoClassePrincipal() {
		$('form:classePrincipal').value = '';
		$('form:classePrincipal').focus();
	}

	function marcarLinhaTabela(){
		
		J(".pintarLinha tbody tr").mouseover(
				function(){
					J(this).css("backgroundColor", "#C4D2EB");
					J(this).css("font-weight", "bold");
	    		}
		);
		
		J(".pintarLinha tbody tr").mouseout(
				function(){
					J(this).css("backgroundColor", "");
					J(this).css("font-weight", "");
	    		}
		);
		
	}

	var J = jQuery.noConflict(); 

	J(document).ready(function(){
		 marcarLinhaTabela();
	});

</script>


<h2>  <ufrn:subSistema /> > Classificação Bibliográfica</h2>

<div class="descricaoOperacao" style="width:80%;">
	
	<p>Utilize este formulário para informar os dados de um inventários do acervo para uma biblioteca do sistema.</p>
	<br/>
	<p>Só pode haver um inventário aberto por biblioteca. Portanto, para cadastrar um novo inventário o anterior deve ser fechado antes.</p>
	<p>Para uma mesma biblioteca, não pode haver dois inventários com a mesma descrição.</p>
	
</div>

<f:view>
	<a4j:keepAlive beanName="inventarioAcervoBibliotecaMBean" />
	
	<br>
	<h:form id="form">
		<h:inputHidden value="#{inventarioAcervoBibliotecaMBean.obj.id}"/>

		<table class="formulario" style="width: 70%">
		
			<caption>Inventário Acervo</caption>
			
			<tr>
				<th class="obrigatorio">Biblioteca:</th>
				<td>
					<h:selectOneMenu id="comboboxBibliotecasInventario" value="#{inventarioAcervoBibliotecaMBean.idBibliotecaSelecionada}" 
						disabled="#{inventarioAcervoBibliotecaMBean.obj.quantidadeMateriaisRegistrados > 0 }"
						required="true" title="Biblioteca">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1" />
						<f:selectItems value="#{inventarioAcervoBibliotecaMBean.allPermissaoUsuarioCombo}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tr>
				<th>Coleção:</th>
				<td>
					<h:selectOneMenu id="comboboxColecaoInventario" value="#{inventarioAcervoBibliotecaMBean.idColecaoSelecionada}" 
					 			disabled="#{inventarioAcervoBibliotecaMBean.obj.quantidadeMateriaisRegistrados > 0 }"
					 			title="Coleção">
						<f:selectItem itemLabel="-- TODAS --" itemValue="-1" />
						<f:selectItems value="#{colecaoMBean.allCombo}"/>
					</h:selectOneMenu>
					<ufrn:help>Selecione uma coleção para fazer um inventário específicos para os materiais de uma determinado coleção do acervo. Caso a coleção não seja escolhida, o inventário será um inventário geral da biblioteca.</ufrn:help>
				</td>
			</tr>
			
			<tr>
				<th class="obrigatorio">Descrição:</th>
				<td><h:inputText value="#{inventarioAcervoBibliotecaMBean.obj.descricao}" readonly="#{inventarioAcervoBibliotecaMBean.readOnly}" required="true" title="Descrição" maxlength="30" size="50"/></td>
			</tr>
			<tr>
				<th class="obrigatorio">Ano:</th>
				<td><h:inputText value="#{inventarioAcervoBibliotecaMBean.obj.ano}" readonly="#{inventarioAcervoBibliotecaMBean.readOnly}" title="Ano" required="true" maxlength="4" size="5" onkeyup="return formatarInteiro(this);"/></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2" align="center">
						<h:commandButton value="#{inventarioAcervoBibliotecaMBean.confirmButton}"  
									action="#{inventarioAcervoBibliotecaMBean.cadastrar}"
									id="acao"/>
									
						<h:commandButton value="Cancelar"  action="#{inventarioAcervoBibliotecaMBean.voltar}" immediate="true" id="cancelar" onclick="#{confirm}"  />						
					</td>
				</tr>
			</tfoot>
		</table>
		
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp" %>
	</h:form>

</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>