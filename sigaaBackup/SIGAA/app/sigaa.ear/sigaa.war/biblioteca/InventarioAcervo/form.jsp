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


<h2>  <ufrn:subSistema /> > Classifica��o Bibliogr�fica</h2>

<div class="descricaoOperacao" style="width:80%;">
	
	<p>Utilize este formul�rio para informar os dados de um invent�rios do acervo para uma biblioteca do sistema.</p>
	<br/>
	<p>S� pode haver um invent�rio aberto por biblioteca. Portanto, para cadastrar um novo invent�rio o anterior deve ser fechado antes.</p>
	<p>Para uma mesma biblioteca, n�o pode haver dois invent�rios com a mesma descri��o.</p>
	
</div>

<f:view>
	<a4j:keepAlive beanName="inventarioAcervoBibliotecaMBean" />
	
	<br>
	<h:form id="form">
		<h:inputHidden value="#{inventarioAcervoBibliotecaMBean.obj.id}"/>

		<table class="formulario" style="width: 70%">
		
			<caption>Invent�rio Acervo</caption>
			
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
				<th>Cole��o:</th>
				<td>
					<h:selectOneMenu id="comboboxColecaoInventario" value="#{inventarioAcervoBibliotecaMBean.idColecaoSelecionada}" 
					 			disabled="#{inventarioAcervoBibliotecaMBean.obj.quantidadeMateriaisRegistrados > 0 }"
					 			title="Cole��o">
						<f:selectItem itemLabel="-- TODAS --" itemValue="-1" />
						<f:selectItems value="#{colecaoMBean.allCombo}"/>
					</h:selectOneMenu>
					<ufrn:help>Selecione uma cole��o para fazer um invent�rio espec�ficos para os materiais de uma determinado cole��o do acervo. Caso a cole��o n�o seja escolhida, o invent�rio ser� um invent�rio geral da biblioteca.</ufrn:help>
				</td>
			</tr>
			
			<tr>
				<th class="obrigatorio">Descri��o:</th>
				<td><h:inputText value="#{inventarioAcervoBibliotecaMBean.obj.descricao}" readonly="#{inventarioAcervoBibliotecaMBean.readOnly}" required="true" title="Descri��o" maxlength="30" size="50"/></td>
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