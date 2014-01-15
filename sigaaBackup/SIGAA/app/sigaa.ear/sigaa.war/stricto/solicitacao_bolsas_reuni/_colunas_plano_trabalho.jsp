<td style="font-variant: small-caps;" nowrap="nowrap"> Linha de Ação <h:outputText value="#{_plano.linhaAcao}"/> </td>
<td> <h:outputText value="#{_plano.descricaoNivel}"/> </td>

<td> 
	<style>
		table.subTabela tbody { background: transparent; }
		table.subTabela { width: 100% } 
	</style>

	<a4j:outputPanel rendered="#{ _plano.linhaAcao == 1}">
		<table class="subTabela">
			<tr>
				<td width="30%"> <i>Componente Curricular:</i>  </td>
				<td> <h:outputText value="#{_plano.componenteCurricular.codigoNome}" /> </td>
			</tr>
			<tr>
				<td valign="top"> <i>Docente(s):</i> </td>
				<td> 
					<a4j:repeat value="#{_plano.docentes}" var="_docente"> 
						<h:outputText value="#{_docente.nome} - #{_docente.unidade.sigla}"/> <br /> 
					</a4j:repeat>
				</td> 	
			</tr>
		</table>
	</a4j:outputPanel>
	<a4j:outputPanel rendered="#{ _plano.linhaAcao == 2}">
		<table class="subTabela">
			<tr>
				<td width="30%"> <i>Área de Atuação:</i>  </td>
				<td> <h:outputText value="#{_plano.areaConhecimento.denominacao}" /> </td>
			</tr>
		</table>						
	</a4j:outputPanel>
</td>

<td style="text-align: right;"> <h:outputText value="#{_plano.numeroAlunosGraduacaoBeneficiados}"/> </td>
<td> <h:outputText value="#{_plano.descricaoStatus}"/> </td>

<td style="text-align: right;">
	<h:commandButton image="/img/view.gif" title="Visualizar plano de trabalho"
		action="#{planoTrabalhoReuniBean.view}" styleClass="noborder">
		<f:setPropertyActionListener value="#{_plano}" target="#{planoTrabalhoReuniBean.obj}"/>
	</h:commandButton>
</td>