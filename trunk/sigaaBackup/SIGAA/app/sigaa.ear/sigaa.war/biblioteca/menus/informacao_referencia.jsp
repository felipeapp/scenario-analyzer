<%@ taglib prefix="fmt"  uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c"    uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="h"    uri="http://java.sun.com/jsf/html"%>
<%@ taglib prefix="f"    uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="ufrn" uri="/tags/ufrn" %>

<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>


<div class="descricaoOperacao"> 
    Esta Seção facilita a fluência das informações entre as fontes de informação e o usuário.<br/>
    Procura, através de serviços fins, atender às necessidades dos usuários, fornecendo-lhes a informação 
    desejada ou encaminhando-os para locais onde esta poderá ser encontrada.
</div>

<ul>
	
	<%-- 
	<li>@Deprecate Registrar Estatísticas Crescimento Coleção 
	<ul>
		<li><i>(Isso aqui vai ser registrado automaticamente ao salvar um título ou exemplar apagar as classes criadas depois)</i> 
		<h:commandLink value="Cadastrar crescimento da Coleção" action="#{registrarCrescimentoAcervo.iniciarRegistroCrescimentoAcervo}"  onclick="setAba('informacao_referencia')"/></li>  </li>
	</ul>
	<ul>
		<li>SubItem</li>
	</ul>
	</li>
	

	<li>Solicitar Revisão de Trabalho Acadêmicos --%>
	<%-- 
	<ul>
		<li>SubItem</li>
	</ul>
	<ul>
		<li>SubItem</li>
	</ul>
	</li>
	--%>
	
	<%--
	<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO
        			, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF} %>">
					
		<li>Levantamento Bibliográfico
			<ul>
				<li><h:commandLink value="Gerenciar Solicitações de Levantamento Bibliográfico e Infra-estrutura" action="#{levantamentoBibliograficoInfraMBean.listarSolicitacoesLevantFuncBiblioteca}"  onclick="setAba('informacao_referencia')"/></li>
			</ul>
		</li>
		
	</ufrn:checkRole>
	--%>

	<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO
        			, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO} %>">

		<li>Gerenciar Solicitações
			<ul>
				
				<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO
       			, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF} %>">
					<li>
						<h:commandLink value="Agendamento de Orientação" action="#{solicitacaoOrientacaoMBean.verSolicitacoes}"  onclick="setAba('informacao_referencia')" />
					</li>
				</ufrn:checkRole>
				
				<%-- OBSERVAÇÃO: O bibliotecário de catalogação deve acessar esse link atender as solicitações de catalogação na fonte --%>
				<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO
       			, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO} %>">
					<li>
						<h:commandLink value="Normalização e Catalogação na Fonte" action="#{solicitacaoServicoDocumentoMBean.verSolicitacoes}"  onclick="setAba('informacao_referencia')"/>
					</li>
				</ufrn:checkRole>
			</ul>
		</li>

	</ufrn:checkRole>
	
	<%-- Comentado por enquanto, caso de uso foi suspenso
	<ufrn:checkRole papeis="<%= new int[] {SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF} %>" >
		<li>
			<h:commandLink value="Solicitações de Levantamento de Infra-Estrutura"
					action="#{levantamentoInfraMBean.listarParaBibliotecario}"
					 onclick="setAba('informacao_referencia')"  />
		</li>
	</ufrn:checkRole>
	--%>
	
	
	<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL} %>">
		
		
		<li>Empréstimos Institucionais
			<ul>
				<li>Bibliotecas/Unidades Internas
					<ul>
						<li>
							<h:commandLink value="Listar/Realizar Novos Empréstimos" action="#{emprestimoInstitucionalMBean.listar}"  onclick="setAba('informacao_referencia')">
								<f:param name="emprestimoParaBibliotecaExterna" value="false" />
								<f:param name="limpaLista" value="true" />
							</h:commandLink>
						</li>
					</ul>
				</li>
				<li>Bibliotecas/Unidades Externas
					<ul>
						<li>
							<h:commandLink value="Listar/Realizar Novos Empréstimos" action="#{emprestimoInstitucionalMBean.listar}"  onclick="setAba('informacao_referencia')">
								<f:param name="emprestimoParaBibliotecaExterna" value="true" />
								<f:param name="limpaLista" value="true" />
							</h:commandLink>
						</li>
					</ul>
				</li>
				
				
				<%-- Mesmos gerenciamento de circulação podem ser feitos aqui para bibliotecas --%>
				
				<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL}  %>">
					<li>
						<h:commandLink value="Gerenciar Suspensões" action="#{suspensaoUsuarioBibliotecaMBean.iniciarGerenciaSuspensoesBibliotecas}" onclick="setAba('informacao_referencia')" />
					</li>
				</ufrn:checkRole>
				
				<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL}  %>">
					<li>
						<h:commandLink value="Gerenciar Multas" action="#{multasUsuarioBibliotecaMBean.iniciarGerenciarMultasBibliotecas}" onclick="setAba('informacao_referencia')" />
					</li>
				</ufrn:checkRole>
				
				<li>
					<h:commandLink value="Bloquear/Desbloquear Usuários" action="#{bloquearUsuarioBibliotecaMBean.iniciarBloqueioBibliotecas}"  onclick="setAba('informacao_referencia')" />
				</li>
				
			</ul>
				
		</li>
		
		<li>Bibliotecas/Unidades Externas
			<ul>
				<li><h:commandLink action="#{bibliotecaExternaMBean.listar}" onclick="setAba('informacao_referencia')" value="Listar / Cadastrar Nova Biblioteca ou Unidade Externa" /></li>
			</ul>
		</li>
		
	</ufrn:checkRole>
	
	
	
	<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL} %>">
		<li> Transferência de Materiais
			<ul>
				
				<li> <h:commandLink action="#{transfereExemplaresEntreBibliotecasMBean.iniciarTransferencia}" value="Transferir Exemplares entre Bibliotecas"
						onclick="setAba('informacao_referencia')" id="cmdTransferirExemplaresEntreBibliotecasInfRef"/> </li>
				
			</ul>	
		</li>
	</ufrn:checkRole>
	
	
	<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL}  %>">
		<li>Materiais Perdidos
			<ul>
				<li>
					<h:commandLink value="Comunicar Material Perdido" action="#{comunicarMaterialPerdidoMBean.iniciarComunicacaoBiblioteca}" onclick="setAba('informacao_referencia')"/>
				</li>
				<li>
					<h:commandLink value="Listar Comunicações de Materiais Perdidos" action="#{comunicarMaterialPerdidoMBean.listarBibliotecasComMateriaisPerdidos}" onclick="setAba('informacao_referencia')" />
				</li>
			</ul>
			
		</li>
	</ufrn:checkRole>
	
	<%--  <li>Registrar Estatísticas Material
		<ul>
			<li><h:commandLink value="Cadastrar Movimentação de Consultas Diárias" action="#{movimentDiarioConsultaMateriais.iniciarCadastroMovimentacaoDiaria}"  onclick="setAba('informacao_referencia')"/></li>
			<li><h:commandLink value="Cadastrar Movimentação de Consultas Diárias Usando Leitor Ótico" action="#{registroConsultaMaterialLeitor.preCadastrar}"  onclick="setAba('informacao_referencia')"/></li>
		</ul>
	</li>  --%>

</ul>